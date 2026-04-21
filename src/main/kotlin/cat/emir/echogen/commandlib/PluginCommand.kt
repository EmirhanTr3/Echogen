package cat.emir.echogen.commandlib

import cat.emir.echogen.commandlib.CommandLib.CommandAction
import cat.emir.echogen.commandlib.CommandLib.CommandBuilder
import cat.emir.echogen.Echogen
import cat.emir.echogen.utils.TimeUtils
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import org.bukkit.entity.Player
import java.time.Duration

/**
 * returns the executor player if present
 */
fun CommandContext<CommandSourceStack>.getPlayer(): Player? {
    if (this.source.sender is Player) return this.source.sender as Player
    this.source.sender.sendRichMessage("<red>You cannot run this command as console.</red>")
    return null
}

/**
 * returns the player from a player argument with provided name
 */
fun CommandContext<CommandSourceStack>.getPlayer(argument: String): Player? {
    val player = this.getArgument(argument, PlayerSelectorArgumentResolver::class.java).resolve(this.source).firstOrNull()
    if (player == null) {
        this.source.sender.sendRichMessage("<red>No player was found</red>")
        return null
    }
    return player
}

fun CommandContext<CommandSourceStack>.getPlayers(argument: String): List<Player>? {
    val players = this.getArgument(argument, PlayerSelectorArgumentResolver::class.java).resolve(this.getSource())
    if (players.isEmpty()) {
        this.source.sender.sendRichMessage("<red>No player was found</red>")
        return null
    }
    return players
}

fun CommandContext<CommandSourceStack>.getDuration(argument: String): Duration? {
    val durationString = StringArgumentType.getString(this, argument)
    val duration = TimeUtils.convertStringToDuration(durationString)

    if (duration == null) {
        this.source.sender.sendRichMessage("<red>Invalid duration provided</red>")
        return null
    }

    return duration
}

abstract class PluginCommand {
    val plugin = Echogen.instance

    open val aliases = setOf<String>()
    
    fun command(
        name: String,
        setup: CommandBuilder.(LiteralArgumentBuilder<CommandSourceStack>) -> Unit
    ): LiteralArgumentBuilder<CommandSourceStack> {
        val builder = CommandBuilder(name)
        builder.setup(builder.node)
        return builder.node
    }

    /**
     * for java compatibility
     */
    fun command(name: String, setup: CommandAction<CommandBuilder>): LiteralArgumentBuilder<CommandSourceStack> {
        val builder = CommandBuilder(name)
        setup.accept(builder)
        return builder.node
    }

    open fun meetsRequirements(): Boolean = true

    abstract fun getCommand(): LiteralArgumentBuilder<CommandSourceStack>

}