package cat.emir.echogen.managers

import io.leangen.geantyref.TypeToken
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting
import org.spongepowered.configurate.serialize.SerializationException

import fr.mrmicky.fastboard.adventure.FastBoard
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import cat.emir.echogen.Echogen
import cat.emir.echogen.toComponent
import cat.emir.echogen.toComponentList
import java.util.UUID
import java.util.regex.Pattern

class ScoreboardManager(val plugin: Echogen) : Listener {
    val boards = mutableMapOf<UUID, FastBoard>()
    var task: BukkitTask? = null
    var enabled = false

    fun load() {
        enabled = plugin.config.rootNode.node("scoreboard", "enabled").boolean
        toggle(enabled)
    }

    fun toggle(state: Boolean) {
        if (state) {
            if (this.task != null)
                toggle(false)
            this.enabled = true
            this.run()
            for (player in plugin.server.onlinePlayers) {
                boards[player.uniqueId] = FastBoard(player)
            }
        } else {
            if (this.task == null)
                return
            this.enabled = false
            this.task!!.cancel()
            this.task = null
            for (board in this.boards.values) {
                board.delete()
            }
            this.boards.clear()
        }
    }

    fun run() {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, Runnable {
            for (board in boards.values) {
                updateBoard(board)
            }
        }, 0, 1)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!enabled)
            return

        val player = event.player
        val board = FastBoard(player)

        this.boards[player.uniqueId] = board
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (!enabled)
            return

        val player = event.player
        val board = this.boards.remove(player.uniqueId)

        board?.delete()
    }

    fun updateBoard(board: FastBoard) {
        val player = board.player

        val tagResolvers = listOf(
            plugin.miniMessageUtils.animatedGradientTag(),
            Placeholder.unparsed("player", player.name))

        val title = parsePAPI(player, plugin.config.rootNode.node("scoreboard", "title").string!!)
            .toComponent(*tagResolvers.toTypedArray())

        val lines = parseLines(board, tagResolvers)

        board.updateTitle(title)
        board.updateLines(lines)
    }

    fun parsePAPI(player: Player, string: String): String {
        return if (plugin.isPAPIEnabled)
            PlaceholderAPI.setPlaceholders(player, string)
        else string
    }

    fun parseLines(board: FastBoard, tagResolvers: List<TagResolver>): List<Component> {
        val player = board.player

        val rootNode = plugin.config.rootNode
        val lines = rootNode.node("scoreboard", "lines").getList(String::class.java)!!
        val originalLines = lines.stream().toList()
        val replacements = rootNode.node("scoreboard", "replacements")
            .getList(object : TypeToken<ReplacementValue>(){})!!

        replacementLoop@ for (replacementElement in replacements) {
            val line = replacementElement.line
            val conditions = replacementElement.condition
            val replacement = replacementElement.replacement

            for (condition in conditions) {
                val matcher = Pattern.compile("(.*) ?(==|!=|<|>|<=|>=) ?(.*)").matcher(condition)
                if (matcher.find()) {
                    val placeholder = matcher.group(1).trim()
                    val operation = matcher.group(2)
                    val value = matcher.group(3).trim()
                    val placeholderValue = parsePAPI(player, placeholder)

                    val result = when (operation) {
                        "==" -> placeholderValue == value
                        "!=" -> placeholderValue != value
                        "<" -> placeholderValue.toDouble() < value.toDouble()
                        ">" -> placeholderValue.toDouble() > value.toDouble()
                        "<=" -> placeholderValue.toDouble() <= value.toDouble()
                        ">=" -> placeholderValue.toDouble() >= value.toDouble()
                        else -> false
                    }

                    if (!result) continue@replacementLoop
                }
            }

            var realLineNumber = line - 1

            if (replacementElement.line != 0) {
                for (lLine in lines) {
                    if (lLine.equals(originalLines[realLineNumber])) {
                        realLineNumber = lines.indexOf(lLine)
                        break
                    }
                }
            }

            when (replacementElement.mode) {
                ReplacementMode.SET -> {
                    if (lines[realLineNumber] != null) {
                        if (!replacementElement.regex.isEmpty()) {
                            lines[realLineNumber] = lines[realLineNumber].replace(replacementElement.regex(), replacement)
                        } else {
                            lines[realLineNumber] = replacement;
                        }
                    }
                }
                ReplacementMode.DELETE ->  {
                    if (lines[realLineNumber] != null) {
                        lines.removeAt(realLineNumber)
                    }
                }
                ReplacementMode.ADD -> lines.add(replacement)
                ReplacementMode.INSERT -> {
                    val firstPart = lines.subList(0, realLineNumber).stream().toList()
                    val secondPart = lines.subList(realLineNumber, lines.size).stream().toList()
                    lines.clear()
                    lines.addAll(firstPart)
                    lines.add(replacement)
                    lines.addAll(secondPart)
                }
            }
        }

        return parsePAPI(player, lines.joinToString("\n"))
            .toComponentList(*tagResolvers.toTypedArray())
    }

    @ConfigSerializable
    data class ReplacementValue(
        @Setting val line: Int = -1,
        @Setting val mode: ReplacementMode = ReplacementMode.SET,
        @Setting val condition: List<String> = emptyList(),
        @Setting val regex: String = "",
        @Setting val replacement: String = ""
    ) {
        fun regex() = regex.toRegex()
    }
    
    enum class ReplacementMode {
        SET,
        DELETE,
        ADD,
        INSERT
    }
}
