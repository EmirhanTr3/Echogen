package cat.emir.echogen.task

import cat.emir.echogen.Echogen
import cat.emir.echogen.toComponent
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player

import java.lang.management.ManagementFactory
import kotlin.math.max
import kotlin.math.min

class RamBarTask private constructor(plugin: Echogen) : BossBarTask(plugin) {
    companion object {
        private var instance: RamBarTask? = null

        fun instance(plugin: Echogen): RamBarTask {
            return instance ?: RamBarTask(plugin).also { instance = it }
        }
    }
    var allocated = 0L
    var used = 0L
    var xmx = 0L
    var xms = 0L
    var percent = 0F
    var tick = 0

    override fun createBossBar(): BossBar {
        return BossBar.bossBar(Component.text(""), 0.0F, instance(plugin).getBossBarColor(), BossBar.Overlay.NOTCHED_20)
    }

    override fun updateBossBar(bossbar: BossBar, player: Player) {
        bossbar.progress(getBossBarProgress())
        bossbar.color(getBossBarColor())
        bossbar.name("<gray>Ram<yellow>:</yellow> <used>/<xmx> (<percent>)".toComponent(
            Placeholder.component("allocated", format(this.allocated)),
            Placeholder.component("used", format(this.used)),
            Placeholder.component("xmx", format(this.xmx)),
            Placeholder.component("xms", format(this.xms)),
            Placeholder.unparsed("percent", (this.percent * 100).toInt().toString() + "%")
        ))
    }

    override fun run() {
        if (++this.tick < 20) {
            return
        }
        this.tick = 0

        val heap = ManagementFactory.getMemoryMXBean().heapMemoryUsage

        this.allocated = heap.committed
        this.used = heap.used
        this.xmx = heap.max
        this.xms = heap.init
        this.percent = (this.used.toFloat() / this.xmx).coerceIn(0.0F, 1.0F)

        super.run()
    }

    fun getBossBarProgress(): Float {
        return this.percent
    }

    fun getBossBarColor(): BossBar.Color {
        return if (this.percent < 0.5F) BossBar.Color.GREEN
        else if (this.percent < 0.75F) BossBar.Color.YELLOW
        else BossBar.Color.RED
    }

    fun format(v: Long): Component {
        val color = if (this.percent < 0.60F) "<gradient:#55ff55:#00aa00><text></gradient>"
        else if (this.percent < 0.85F) "<gradient:#ffff55:#ffaa00><text></gradient>"
        else "<gradient:#ff5555:#aa0000><text></gradient>"

        val value = if (v < 1024) {
            "${v}B"
        } else {
            val z = (63 - v.countLeadingZeroBits()) / 10
            "%.1f%s".format(v.toDouble() / (1L shl (z * 10)), "BKMGTPE"[z])
        }

        return color.toComponent(Placeholder.unparsed("text", value))
    }
}