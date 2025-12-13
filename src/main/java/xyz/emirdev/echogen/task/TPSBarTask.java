package xyz.emirdev.echogen.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPSBarTask extends BossBarTask {
    private static TPSBarTask instance;
    private double tps = 20.0D;
    private double mspt = 0.0D;
    private int tick = 0;

    public static TPSBarTask instance() {
        if (instance == null) {
            instance = new TPSBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, instance().getBossBarColor(), BossBar.Overlay.NOTCHED_20);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(getBossBarProgress());
        bossbar.color(getBossBarColor());
        bossbar.name(MiniMessage.miniMessage().deserialize("<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms",
                Placeholder.component("tps", getTPSColor()),
                Placeholder.component("mspt", getMSPTColor()),
                Placeholder.component("ping", getPingColor(player.getPing()))
        ));
    }

    @Override
    public void run() {
        if (++tick < 20) {
            return;
        }
        tick = 0;

        this.tps = Math.max(Math.min(Bukkit.getTPS()[0], 20.0D), 0.0D);
        this.mspt = Bukkit.getAverageTickTime();

        super.run();
    }

    private float getBossBarProgress() {
        return Math.max(Math.min((float) mspt / 50.0F, 1.0F), 0.0F);
    }

    private BossBar.Color getBossBarColor() {
        if (isGood(FillMode.MSPT)) {
            return BossBar.Color.GREEN;
        } else if (isMedium(FillMode.MSPT)) {
            return BossBar.Color.YELLOW;
        } else {
            return BossBar.Color.RED;
        }
    }

    private boolean isGood(FillMode mode) {
        return isGood(mode, 0);
    }

    private boolean isGood(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 40;
        } else if (mode == FillMode.TPS) {
            return tps >= 19;
        } else if (mode == FillMode.PING) {
            return ping < 100;
        } else {
            return false;
        }
    }

    private boolean isMedium(FillMode mode) {
        return isMedium(mode, 0);
    }

    private boolean isMedium(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 50;
        } else if (mode == FillMode.TPS) {
            return tps >= 15;
        } else if (mode == FillMode.PING) {
            return ping < 200;
        } else {
            return false;
        }
    }

    private Component getTPSColor() {
        String color;
        if (isGood(FillMode.TPS)) {
            color = "<gradient:#55ff55:#00aa00><text></gradient>";
        } else if (isMedium(FillMode.TPS)) {
            color = "<gradient:#ffff55:#ffaa00><text></gradient>";
        } else {
            color = "<gradient:#ff5555:#aa0000><text></gradient>";
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", tps)));
    }

    private Component getMSPTColor() {
        String color;
        if (isGood(FillMode.MSPT)) {
            color = "<gradient:#55ff55:#00aa00><text></gradient>";
        } else if (isMedium(FillMode.MSPT)) {
            color = "<gradient:#ffff55:#ffaa00><text></gradient>";
        } else {
            color = "<gradient:#ff5555:#aa0000><text></gradient>";
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", mspt)));
    }

    private Component getPingColor(int ping) {
        String color;
        if (isGood(FillMode.PING, ping)) {
            color = "<gradient:#55ff55:#00aa00><text></gradient>";
        } else if (isMedium(FillMode.PING, ping)) {
            color = "<gradient:#ffff55:#ffaa00><text></gradient>";
        } else {
            color = "<gradient:#ff5555:#aa0000><text></gradient>";
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%s", ping)));
    }

    public enum FillMode {
        TPS, MSPT, PING
    }
}