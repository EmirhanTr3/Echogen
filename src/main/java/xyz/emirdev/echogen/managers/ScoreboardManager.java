package xyz.emirdev.echogen.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
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
        this.enabled = state;
        if (state) {
            if (this.task != null)
                toggle(false);
            this.run();
            for (Player player : Bukkit.getOnlinePlayers()) {
                boards.put(player.getUniqueId(), new FastBoard(player));
            }
        } else {
            if (this.task == null)
                return;
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

        List<Component> lines = Utils.formatMultiline(
                parsePAPI(player, String.join("\n",
                        Echogen.get().getPluginConfig().getRootNode().node("scoreboard", "lines")
                                .getList(String.class))),
                tagResolvers.toArray(new TagResolver[0]));

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
}
