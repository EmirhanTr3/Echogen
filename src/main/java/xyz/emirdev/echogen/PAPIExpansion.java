package xyz.emirdev.echogen;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.variables.Variables;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    @NotNull
    public String getAuthor() {
        return "EmirhanTr3";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "echogen";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || !player.isOnline())
            return null;

        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());
        assert onlinePlayer != null;

        if (params.startsWith("sk_")) {
            if (!Echogen.get().isSkriptEnabled()) return null;
            return skriptPlaceholders(onlinePlayer, params);
        }

        return switch (params.toLowerCase()) {
            case "vanished" -> String.valueOf(Echogen.get().getVanishManager().isVanished(onlinePlayer));
            case "prefix" -> Objects.requireNonNullElse(
                    Echogen.get().getPrefixManager().getPlayerPrefixString(onlinePlayer), "");
            default -> null;
        };
    }

    private String skriptPlaceholders(Player player, String params) {
        String variable = params.substring("sk_".length());
        if (variable.startsWith("function_")) {
            String function = variable.substring("function_".length());

            Object[][] funcParams = {{ player }};
            Function<?> func = Functions.getGlobalFunction(function);
            if (func == null) return null;

            Object[] returnValue = func.execute(funcParams);
            if (returnValue == null || returnValue.length == 0) return null;

            return String.valueOf(returnValue[0]);
        }

        String defaultValue = null;
        if (variable.contains("_?_")) {
            String[] split = variable.split("_\\?_");
            if (split.length != 2) return null;

            variable = split[0];
            defaultValue = split[1];
        }

        Pattern pattern = Pattern.compile("[{\\[](.*?)[}\\]]");
        Matcher matcher = pattern.matcher(variable);

        while (matcher.find()) {
            String match = matcher.group(0);
            String placeholder = matcher.group(1);
            variable = variable.replace(match, PlaceholderAPI.setPlaceholders(player, "%" + placeholder + "%"));
        }

        Object value = Variables.getVariable(variable, null, false);
        if (value == null && defaultValue != null) return defaultValue;
        return String.valueOf(value);
    }
}
