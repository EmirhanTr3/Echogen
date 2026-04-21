package cat.emir.echogen

import cat.emir.echode.ClassUtils
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.database.PrefixDatabase
import cat.emir.echogen.managers.FilterManager
import cat.emir.echogen.managers.PrefixManager
import cat.emir.echogen.managers.ScoreboardManager
import cat.emir.echogen.managers.ScoreboardManager.ReplacementValue
import cat.emir.echogen.managers.VanishManager
import cat.emir.echogen.task.BossBarTask
import cat.emir.echogen.utils.LuckPermsUtils
import cat.emir.echogen.utils.MiniMessageUtils
import io.leangen.geantyref.TypeToken
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.spongepowered.configurate.ConfigurationNode
import java.lang.reflect.InvocationTargetException

fun String.toComponent(vararg resolvers: TagResolver): Component {
    return MiniMessage.miniMessage().deserialize(this, *resolvers)
}

fun String.toComponentList(vararg resolvers: TagResolver): List<Component> {
    val components = mutableListOf<Component>()
    val strings = this.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    for (string in strings) {
        components.add(MiniMessage.miniMessage().deserialize(string, *resolvers))
    }

    return components
}

class Echogen : JavaPlugin() {
    companion object {
        lateinit var instance: Echogen
            private set
    }
    
    lateinit var luckPerms: LuckPerms
        private set

    val config = PluginConfig(this)
    val scoreboardManager = ScoreboardManager(this)
    val vanishManager = VanishManager(this)
    val filterManager = FilterManager(this)
    val prefixManager = PrefixManager(this)
    val prefixDatabase = PrefixDatabase(this)
    val miniMessageUtils = MiniMessageUtils(this)
    val luckPermsUtils = LuckPermsUtils(this)
    var isPAPIEnabled = false
    var isSkriptEnabled = false
    var isOpenInvEnabled = false

    override fun reloadConfig() {
        this.config.load()
        scoreboardManager.toggle(config.rootNode.node("scoreboard", "enabled").boolean)
        filterManager.load()
        prefixManager.load()
    }

    override fun onEnable() {
        instance = this
        luckPerms = LuckPermsProvider.get()
        isPAPIEnabled = server.pluginManager.isPluginEnabled("PlaceholderAPI")
        isSkriptEnabled = server.pluginManager.isPluginEnabled("Skript")
        isOpenInvEnabled = server.pluginManager.isPluginEnabled("OpenInv")

        config.load()

        scoreboardManager.load()
        server.pluginManager.registerEvents(scoreboardManager, this)

        vanishManager.load()
        server.pluginManager.registerEvents(vanishManager, this)

        filterManager.load()

        prefixManager.load()
        prefixDatabase.load()

        miniMessageUtils.load()

        BossBarTask.startAll(this)

        if (isPAPIEnabled)
            PAPIExpansion(this).register()

        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { manager ->
            val registrar = manager.registrar()
            ClassUtils.findClasses(
                "cat.emir.echogen.commands",
                { it.extendsSuperclass(PluginCommand::class.java) },
                {
                    val command = it.loadClass().asSubclass(PluginCommand::class.java).constructors[0].newInstance() as PluginCommand
                    if (command.meetsRequirements())
                        registrar.register(command.getCommand().build(), command.aliases)
                }
            )
        }

        ClassUtils.findClasses(
            "cat.emir.echogen.events",
            { it.implementsInterface(Listener::class.java) },
            {
                val event = it.loadClass().constructors[0].newInstance(this) as Listener
                Bukkit.getPluginManager().registerEvents(event, this)
            }
        )
    }
}
