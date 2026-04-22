package cat.emir.echogen.task

import net.kyori.adventure.bossbar.BossBar
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import cat.emir.echogen.Echogen

import java.util.UUID

abstract class BossBarTask(val plugin: Echogen) : BukkitRunnable() {
    val bossbars = mutableMapOf<UUID, BossBar>()
    var started: Boolean = false

    abstract fun createBossBar(): BossBar

    abstract fun updateBossBar(bossbar: BossBar, player: Player)

    override fun run() {
        for ((uuid, bossbar) in bossbars.entries) {
            val player = plugin.server.getPlayer(uuid) ?: continue
            updateBossBar(bossbar, player)
        }
    }

    override fun cancel() {
        super.cancel()
        for (uuid in bossbars.keys) {
            val player = plugin.server.getPlayer(uuid) ?: continue
            removePlayer(player)
        }
        bossbars.clear()
    }

    fun removePlayer(player: Player): Boolean {
        val bossbar = bossbars.remove(player.uniqueId) ?: return false
        player.hideBossBar(bossbar)
        return true
    }

    fun addPlayer(player: Player) {
        removePlayer(player)
        val bossbar = createBossBar()
        bossbars[player.uniqueId] = bossbar
        updateBossBar(bossbar, player)
        player.showBossBar(bossbar)
    }

    fun hasPlayer(uuid: UUID): Boolean {
        return this.bossbars.containsKey(uuid)
    }

    fun togglePlayer(player: Player): Boolean {
        if (removePlayer(player)) {
            return false
        }
        addPlayer(player)
        return true
    }

    fun start() {
        stop()
        runTaskTimerAsynchronously(plugin, 1, 1)
        started = true
    }

    fun stop() {
        if (started) {
            cancel()
        }
    }

    companion object {
        fun startAll(plugin: Echogen) {
            RamBarTask.instance(plugin).start()
            TPSBarTask.instance(plugin).start()
        }

        fun stopAll(plugin: Echogen) {
            RamBarTask.instance(plugin).stop()
            TPSBarTask.instance(plugin).stop()
        }
    }
}
