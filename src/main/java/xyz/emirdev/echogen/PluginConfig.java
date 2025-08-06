package xyz.emirdev.echogen;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class PluginConfig {
    private final Echogen plugin;
    private YamlConfigurationLoader loader;
    private CommentedConfigurationNode rootNode;

    public PluginConfig(Echogen plugin) {
        this.plugin = plugin;
    }

    public YamlConfigurationLoader getLoader() {
        return this.loader;
    }

    public CommentedConfigurationNode getRootNode() {
        return this.rootNode;
    }

    public void saveConfig() {
        try {
            this.loader.save(this.rootNode);
        } catch (ConfigurateException ex) {
            this.plugin.getSLF4JLogger().error("Couldn't save config:", ex);
            return;
        }
    }

    public void load() {
        Logger logger = this.plugin.getSLF4JLogger();

        Path configPath = this.plugin.getDataPath().resolve("config.yml");
        if (!Files.exists(configPath)) {
            logger.info("Generating config from plugin...");
            try (InputStream is = this.plugin.getResource("config.yml")) {
                if (is == null) {
                    logger.error("There is no config.yml packaged in the plugin.");
                    return;
                }

                Files.createDirectories(configPath.getParent());
                Files.copy(is, configPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("An error occured while copying the config from the plugin:", e);
                return;
            }
        }

        logger.info("Loading configuration file...");
        loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build();

        try {
            rootNode = loader.load();
        } catch (IOException ex) {
            logger.error("An error occured while loading the configuration:", ex);
        }
    }
}
