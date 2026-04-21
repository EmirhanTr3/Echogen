package cat.emir.echogen

import java.io.IOException
import java.nio.file.Files

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.nio.file.StandardCopyOption
import kotlin.io.path.div

class PluginConfig(val plugin: Echogen) {
    lateinit var loader: YamlConfigurationLoader
    lateinit var rootNode: CommentedConfigurationNode

    fun saveConfig() {
        try {
            loader.save(this.rootNode)
        } catch (e: ConfigurateException) {
            plugin.slF4JLogger.error("Couldn't save config:", e)
        }
    }

    fun load() {
        val logger = plugin.slF4JLogger

        val configPath = this.plugin.dataPath / "config.yml"
        if (!Files.exists(configPath)) {
            logger.info("Generating config from plugin...")
            try {
                this.plugin.getResource("config.yml").use { inputStream ->
                    if (inputStream == null) {
                        logger.error("There is no config.yml packaged in the plugin.")
                        return
                    }

                    Files.createDirectories(configPath.getParent())
                    Files.copy(inputStream, configPath, StandardCopyOption.REPLACE_EXISTING)
                }
            } catch (e: IOException) {
                logger.error("An error occured while copying the config from the plugin:", e)
                return
            }
        }

        logger.info("Loading configuration file...")
        loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .build()

        try {
            rootNode = loader.load()
        } catch (e: IOException) {
            logger.error("An error occured while loading the configuration:", e)
        }
    }
}
