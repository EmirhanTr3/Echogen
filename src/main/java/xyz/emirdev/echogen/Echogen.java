package xyz.emirdev.echogen;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import xyz.emirdev.echogen.commands.ChatCommand;
import xyz.emirdev.echogen.commands.FlyCommand;
import xyz.emirdev.echogen.commands.MainCommand;
import xyz.emirdev.echogen.commands.SmiteCommand;
import xyz.emirdev.echogen.commands.VanishCommand;
import xyz.emirdev.echogen.events.ChatEvent;
import xyz.emirdev.echogen.managers.FilterManager;
import xyz.emirdev.echogen.managers.ScoreboardManager;
import xyz.emirdev.echogen.managers.VanishManager;
import xyz.emirdev.echogen.utils.MiniMessageUtils;

public class Echogen extends JavaPlugin {
    private static Echogen instance;
    private LuckPerms luckPerms;
    private PluginConfig config;
    private ScoreboardManager scoreboardManager;
    private VanishManager vanishManager;
    private FilterManager filterManager;
    private MiniMessageUtils miniMessageUtils;
    private boolean isPAPIEnabled;

    public static Echogen get() {
        return instance;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    public MiniMessageUtils getMiniMessageUtils() {
        return miniMessageUtils;
    }

    public boolean isPAPIEnabled() {
        return isPAPIEnabled;
    }

    public void reloadConfig() {
        this.config.load();
        scoreboardManager.toggle(config.getRootNode().node("scoreboard", "enabled").getBoolean());
        filterManager.load();
    }

    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();
            registrar.register(new MainCommand().getCommand());
            registrar.register(new FlyCommand().getCommand());
            registrar.register(new SmiteCommand().getCommand());
            registrar.register(new SmiteCommand().getSecondaryCommand());
            registrar.register(new VanishCommand().getCommand());
            registrar.register(new ChatCommand().getCommand());
        });
    }

    private void registerEvents() {
        List.of(
                new ChatEvent()).forEach(e -> Bukkit.getPluginManager().registerEvents(e, this));
    }

    @Override
    public void onEnable() {
        instance = this;
        registerCommands();
        registerEvents();
        luckPerms = LuckPermsProvider.get();
        isPAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        config = new PluginConfig(this);
        config.load();

        scoreboardManager = new ScoreboardManager();
        Bukkit.getPluginManager().registerEvents(scoreboardManager, this);

        vanishManager = new VanishManager();
        Bukkit.getPluginManager().registerEvents(vanishManager, this);

        filterManager = new FilterManager();

        miniMessageUtils = new MiniMessageUtils();
        miniMessageUtils.run();

        new PAPIExpansion().register();
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
