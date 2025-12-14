package xyz.emirdev.echogen.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.SerializationException;

import fr.mrmicky.fastboard.adventure.FastBoard;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.utils.Utils;

public class ScoreboardManager implements Listener {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private BukkitTask task = null;
    private boolean enabled;

    public ScoreboardManager() {
        enabled = Echogen.get().getPluginConfig().getRootNode().node("scoreboard", "enabled").getBoolean();
        toggle(enabled);
    }

    public void toggle(boolean state) {
        if (state) {
            if (this.task != null)
                toggle(false);
            this.enabled = true;
            this.run();
            for (Player player : Bukkit.getOnlinePlayers()) {
                boards.put(player.getUniqueId(), new FastBoard(player));
            }
        } else {
            if (this.task == null)
                return;
            this.enabled = false;
            this.task.cancel();
            this.task = null;
            for (FastBoard board : this.boards.values()) {
                board.delete();
            }
            this.boards.clear();
        }
    }

    public void run() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(Echogen.get(), () -> {
            for (FastBoard board : boards.values()) {
                try {
                    updateBoard(board);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!enabled)
            return;
        Player player = e.getPlayer();

        FastBoard board = new FastBoard(player);

        this.boards.put(player.getUniqueId(), board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!enabled)
            return;
        Player player = e.getPlayer();

        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(FastBoard board) throws SerializationException {
        Player player = board.getPlayer();

        List<TagResolver> tagResolvers = List.of(
                Echogen.get().getMiniMessageUtils().animatedGradientTag(),
                Placeholder.unparsed("player", player.getName()));

        Component title = Utils.formatMessage(
                parsePAPI(player,
                        Echogen.get().getPluginConfig().getRootNode().node("scoreboard", "title").getString()),
                tagResolvers.toArray(new TagResolver[0]));

        List<Component> lines = parseLines(board, tagResolvers);

        board.updateTitle(title);
        board.updateLines(lines);
    }

    private String parsePAPI(Player player, String string) {
        if (Echogen.get().isPAPIEnabled()) {
            return PlaceholderAPI.setPlaceholders(player, string);
        } else {
            return string;
        }
    }

    private List<Component> parseLines(FastBoard board, List<TagResolver> tagResolvers) throws SerializationException {
        Player player = board.getPlayer();

        CommentedConfigurationNode rootNode = Echogen.get().getPluginConfig().getRootNode();
        List<String> lines = rootNode.node("scoreboard", "lines").getList(String.class);
        List<String> originalLines = lines.stream().toList();
        List<ReplacementValue> replacements = rootNode.node("scoreboard", "replacements").getList(new TypeToken<>(){});

        replacementLoop:
        for (ReplacementValue replacementElement : replacements) {
            int line = replacementElement.getLine();
            List<String> conditions = replacementElement.getCondition();
            String replacement = replacementElement.getReplacement();

            for (String condition : conditions) {
                Matcher matcher = Pattern.compile("(.*) ?(==|!=|<|>|<=|>=) ?(.*)").matcher(condition);
                if (matcher.find()) {
                    String placeholder = matcher.group(1).trim();
                    String operation = matcher.group(2);
                    String value = matcher.group(3).trim();
                    String placeholderValue = parsePAPI(player, placeholder);

                    boolean result = switch (operation) {
                        case "==" -> placeholderValue.equals(value);
                        case "!=" -> !placeholderValue.equals(value);
                        case "<" -> Double.parseDouble(placeholderValue) < Double.parseDouble(value);
                        case ">" -> Double.parseDouble(placeholderValue) > Double.parseDouble(value);
                        case "<=" -> Double.parseDouble(placeholderValue) <= Double.parseDouble(value);
                        case ">=" -> Double.parseDouble(placeholderValue) >= Double.parseDouble(value);
                        default -> false;
                    };

                    if (!result) continue replacementLoop;
                }
            }

            int realLineNumber = line - 1;

            if (replacementElement.getLine() != 0) {
                for (String lLine : lines) {
                    if (lLine.equals(originalLines.get(realLineNumber))) {
                        realLineNumber = lines.indexOf(lLine);
                        break;
                    }
                }
            }

            switch (replacementElement.getMode()) {
                case SET -> {
                    if (lines.get(realLineNumber) != null) {
                        if (replacementElement.getRegex() != null) {
                            lines.set(realLineNumber,
                                    lines.get(realLineNumber).replaceAll(replacementElement.getRegex(), replacement)
                            );
                        } else {
                            lines.set(realLineNumber, replacement);
                        }
                    }
                }
                case DELETE ->  {
                    if (lines.get(realLineNumber) != null) {
                        lines.remove(realLineNumber);
                    }
                }
                case ADD -> lines.add(replacement);
                case INSERT -> {
                    List<String> firstPart = lines.subList(0, realLineNumber).stream().toList();
                    List<String> secondPart = lines.subList(realLineNumber, lines.size()).stream().toList();
                    lines.clear();
                    lines.addAll(firstPart);
                    lines.add(replacement);
                    lines.addAll(secondPart);
                }
            }
        }

        return Utils.formatMultiline(
                parsePAPI(player, String.join("\n", lines)),
                tagResolvers.toArray(new TagResolver[0])
        );
    }

    @Getter
    @ConfigSerializable
    public static class ReplacementValue {
        @Setting
        private int line;
        @Setting
        private ReplacementMode mode = ReplacementMode.SET;
        @Setting
        private List<String> condition;
        @Setting
        private String regex;
        @Setting
        private String replacement;

        public ReplacementValue() {
        }
    }
    
    public enum ReplacementMode {
        SET,
        DELETE,
        ADD,
        INSERT
    }
}
