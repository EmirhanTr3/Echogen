package cat.emir.echogen

import cat.emir.echogen.database.PrefixDatabase
import cat.emir.echogen.managers.FilterManager
import cat.emir.echogen.managers.PrefixManager
import cat.emir.echogen.managers.ScoreboardManager
import cat.emir.echogen.managers.VanishManager
import cat.emir.echogen.task.BossBarTask
import cat.emir.echogen.utils.LuckPermsUtils
import cat.emir.echogen.utils.MiniMessageUtils
import cat.emir.echogen.utils.TimeUtils
import cat.emir.echolib.EchoPlugin
import cat.emir.echolib.PluginConfig
import cat.emir.echolib.command.CommandLib
import cat.emir.echolib.event.EventLoader
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import java.time.Duration

fun CommandContext<CommandSourceStack>.getDuration(argument: String): Duration? {
    val durationString = StringArgumentType.getString(this, argument)
    val duration = TimeUtils.convertStringToDuration(durationString)

    if (duration == null) {
        this.source.sender.sendRichMessage("<red>Invalid duration provided</red>")
        return null
    }

    return duration
}

class Echogen : EchoPlugin() {
    companion object {
        lateinit var instance: Echogen
            private set
    }
    
    lateinit var luckPerms: LuckPerms
        private set

    val config = PluginConfig(this, "config.yml")
    val scoreboardManager = ScoreboardManager(this)
    val vanishManager = VanishManager(this)
    val filterManager = FilterManager(this)
    val prefixManager = PrefixManager(this)
    val prefixDatabase = PrefixDatabase(this, "database")
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

        CommandLib.registerCommands(this, "cat.emir.echogen.commands")
        EventLoader.registerEvents(this, "cat.emir.echogen.events")
    }
}
