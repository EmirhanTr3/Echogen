package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.commandlib.getPlayers
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SpeedCommand : PluginCommand() {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("speed") {
            requires { it.sender.hasPermission("echogen.speed") }
            argument("speed", FloatArgumentType.floatArg(-10f, 10f)) {
                requires { it.sender.hasPermission("echogen.speed.all") }
                executes(::allspeed)
                argument("players", ArgumentTypes.players()) {
                    requires { it.sender.hasPermission("echogen.speed.all.others") }
                    executes(::allspeedOther)
                }
            }
            subcommand("fly") {
                requires { it.sender.hasPermission("echogen.speed.fly") }
                argument("speed", FloatArgumentType.floatArg(-10f, 10f)) {
                    executes(::flyspeed)
                    argument("players", ArgumentTypes.players()) {
                        requires { it.sender.hasPermission("echogen.speed.fly.others") }
                        executes(::flyspeedOther)
                    }
                }
            }
            subcommand("walk") {
                requires { it.sender.hasPermission("echogen.speed.walk") }
                argument("speed", FloatArgumentType.floatArg(-10f, 10f)) {
                    executes(::walkspeed)
                    argument("players", ArgumentTypes.players()) {
                        requires { it.sender.hasPermission("echogen.speed.walk.others") }
                        executes(::walkspeedOther)
                    }
                }
            }
        }
    }

    fun flyspeed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        player.flySpeed = (speed / 10f)

        player.sendRichMessage("<aqua>You have set your flight speed to <speed></aqua>",
            Placeholder.unparsed("speed", speed.toString()))

        return 1
    }

    fun flyspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.flySpeed = (speed / 10f)

            val speedTag = Placeholder.unparsed("speed", speed.toString())
            sender.sendRichMessage("<aqua>You have set flight speed of <player> to <speed>.</aqua>",
                Placeholder.unparsed("player", target.name), speedTag)
            target.sendRichMessage("<aqua>Your flight speed has been set to <speed>.</aqua>", speedTag)
        }

        return 1
    }

    fun walkspeed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        player.walkSpeed = (speed / 10f)

        player.sendRichMessage("<aqua>You have set your walk speed to <speed></aqua>",
            Placeholder.unparsed("speed", speed.toString()))

        return 1
    }

    fun walkspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.walkSpeed = (speed / 10f)

            val speedTag = Placeholder.unparsed("speed", speed.toString())
            sender.sendRichMessage("<aqua>You have set walk speed of <player> to <speed>.</aqua>",
                Placeholder.unparsed("player", target.name), speedTag)
            target.sendRichMessage("<aqua>Your walk speed has been set to <speed>.</aqua>", speedTag)
        }

        return 1
    }

    fun allspeed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        player.flySpeed = (speed / 10f)
        player.walkSpeed = (speed / 10f)

        player.sendRichMessage("<aqua>You have set your speed to <speed></aqua>",
            Placeholder.unparsed("speed", speed.toString()))

        return 1
    }

    fun allspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.flySpeed = (speed / 10f)
            target.walkSpeed = (speed / 10f)

            val speedTag = Placeholder.unparsed("speed", speed.toString())
            sender.sendRichMessage("<aqua>You have set speed of <player> to <speed>.</aqua>",
                Placeholder.unparsed("player", target.name), speedTag)
            target.sendRichMessage("<aqua>Your speed has been set to <speed>.</aqua>", speedTag)
        }

        return 1
    }
}
