package xyz.emirdev.echogen;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import xyz.emirdev.echogen.database.PrefixDatabase;
import xyz.emirdev.echogen.managers.FilterManager;
import xyz.emirdev.echogen.managers.PrefixManager;
import xyz.emirdev.echogen.managers.ScoreboardManager;
import xyz.emirdev.echogen.managers.VanishManager;
import xyz.emirdev.echogen.task.BossBarTask;
import xyz.emirdev.echogen.utils.ClassUtils;
import xyz.emirdev.echogen.utils.MiniMessageUtils;

public class Echogen extends JavaPlugin {
    private static Echogen instance;
    @Getter
    private LuckPerms luckPerms;
    private PluginConfig config;
    @Getter
    private ScoreboardManager scoreboardManager;
    @Getter
    private VanishManager vanishManager;
    @Getter
    private FilterManager filterManager;
    @Getter
    private PrefixManager prefixManager;
    @Getter
    private PrefixDatabase prefixDatabase;
    @Getter
    private MiniMessageUtils miniMessageUtils;
    @Getter
    private boolean isPAPIEnabled;
    @Getter
    private boolean isSkriptEnabled;

    public static Echogen get() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return config;
    }

    public void reloadConfig() {
        this.config.load();
        scoreboardManager.toggle(config.getRootNode().node("scoreboard", "enabled").getBoolean());
        filterManager.load();
        prefixManager.load();
    }

    @Override
    public void onEnable() {
        instance = this;
        luckPerms = LuckPermsProvider.get();
        isPAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        isSkriptEnabled = Bukkit.getPluginManager().isPluginEnabled("Skript");

        config = new PluginConfig(this);
        config.load();

        scoreboardManager = new ScoreboardManager();
        Bukkit.getPluginManager().registerEvents(scoreboardManager, this);

        vanishManager = new VanishManager();
        Bukkit.getPluginManager().registerEvents(vanishManager, this);

        filterManager = new FilterManager();

        prefixManager = new PrefixManager();
        prefixDatabase = new PrefixDatabase();

        miniMessageUtils = new MiniMessageUtils();
        miniMessageUtils.run();

        BossBarTask.startAll();

        if (isPAPIEnabled)
            new PAPIExpansion().register();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();
            ClassUtils.findClasses(
                    "xyz.emirdev.echogen.commands",
                    clazz -> clazz.extendsSuperclass(PluginCommand.class),
                    clazz -> {
                        PluginCommand command;
                        try {
                            command = clazz.loadClass().asSubclass(PluginCommand.class).getConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        registrar.register(command.getCommand(), command.getAliases());
                    }
            );
        });

        ClassUtils.findClasses(
                "xyz.emirdev.echogen.events",
                clazz -> clazz.implementsInterface(Listener.class),
                clazz -> {
                    try {
                        Bukkit.getPluginManager().registerEvents((Listener) clazz.loadClass().getConstructor().newInstance(), this);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
