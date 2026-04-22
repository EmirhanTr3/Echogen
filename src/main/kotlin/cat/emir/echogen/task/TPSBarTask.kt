package cat.emir.echogen.task

import cat.emir.echogen.Echogen
import cat.emir.echogen.toComponent
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class TPSBarTask private constructor(plugin: Echogen) : BossBarTask(plugin) {
    companion object {
        private var instance: TPSBarTask? = null

        fun instance(plugin: Echogen): TPSBarTask {
            return instance ?: TPSBarTask(plugin).also { instance = it }
        }
    }
    var tps = 20.0
    var mspt = 0.0
    var tick = 0

    override fun createBossBar(): BossBar {
        return BossBar.bossBar(Component.text(""), 0.0F, instance(plugin).getBossBarColor(), BossBar.Overlay.NOTCHED_20)
    }

    override fun updateBossBar(bossbar: BossBar, player: Player) {
        bossbar.progress(getBossBarProgress())
        bossbar.color(getBossBarColor())
        bossbar.name("<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms".toComponent(
            Placeholder.component("tps", getTPSColor()),
            Placeholder.component("mspt", getMSPTColor()),
            Placeholder.component("ping", getPingColor(player.ping))
        ))
    }

    override fun run() {
        if (++tick < 20) {
            return
        }
        tick = 0

        this.tps = max(min(plugin.server.tps[0], 20.0), 0.0)
        this.mspt = plugin.server.averageTickTime

        super.run()
    }

    fun getBossBarProgress(): Float {
        return max(min((mspt / 50.0F).toFloat(), 1.0F), 0.0F)
    }

    fun getBossBarColor(): BossBar.Color {
        return if (isGood(FillMode.MSPT)) BossBar.Color.GREEN
        else if (isMedium(FillMode.MSPT)) BossBar.Color.YELLOW
        else BossBar.Color.RED
    }

    fun isGood(mode: FillMode): Boolean {
        return isGood(mode, 0)
    }

    fun isGood(mode: FillMode, ping: Int): Boolean {
        return when (mode) {
            FillMode.MSPT -> mspt < 40
            FillMode.TPS -> tps >= 19
            FillMode.PING -> ping < 100
        }
    }

    fun isMedium(mode: FillMode): Boolean {
        return isMedium(mode, 0)
    }

    fun isMedium(mode: FillMode, ping: Int): Boolean {
        return when (mode) {
            FillMode.MSPT -> mspt < 50
            FillMode.TPS -> tps >= 15
            FillMode.PING -> ping < 200
        }
    }

    fun getTPSColor(): Component {
        val color = if (isGood(FillMode.TPS)) "<gradient:#55ff55:#00aa00><text></gradient>"
        else if (isMedium(FillMode.TPS)) "<gradient:#ffff55:#ffaa00><text></gradient>"
        else "<gradient:#ff5555:#aa0000><text></gradient>"

        return color.toComponent(Placeholder.parsed("text", "%.2f".format(tps)))
    }

    fun getMSPTColor(): Component {
        val color = if (isGood(FillMode.MSPT)) "<gradient:#55ff55:#00aa00><text></gradient>"
        else if (isMedium(FillMode.MSPT)) "<gradient:#ffff55:#ffaa00><text></gradient>"
        else "<gradient:#ff5555:#aa0000><text></gradient>"

        return color.toComponent(Placeholder.parsed("text", "%.2f".format(mspt)))
    }

    fun getPingColor(ping: Int): Component {
        val color = if (isGood(FillMode.PING, ping)) "<gradient:#55ff55:#00aa00><text></gradient>"
        else if (isMedium(FillMode.PING, ping)) "<gradient:#ffff55:#ffaa00><text></gradient>"
        else "<gradient:#ff5555:#aa0000><text></gradient>"

        return color.toComponent(Placeholder.parsed("text", "%s".format(ping)))
    }

    enum class FillMode {
        TPS, MSPT, PING
    }
}