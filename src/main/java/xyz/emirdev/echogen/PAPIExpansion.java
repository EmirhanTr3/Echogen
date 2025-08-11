package xyz.emirdev.echogen;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIExpansion extends PlaceholderExpansion {
    @Override
    public String getAuthor() {
        return "EmirhanTr3";
    }

    @Override
    public String getIdentifier() {
        return "echogen";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.isOnline())
            return null;

        Player onlinePlayer = Bukkit.getPlayer(player.getUniqueId());

        return switch (params.toLowerCase()) {
            case "vanished" -> String.valueOf(Echogen.get().getVanishManager().isVanished(onlinePlayer));
            case "prefix" -> Objects.requireNonNullElse(
                    Echogen.get().getPrefixManager().getPlayerPrefixString(onlinePlayer), "");
            default -> null;
        };
    }
}
