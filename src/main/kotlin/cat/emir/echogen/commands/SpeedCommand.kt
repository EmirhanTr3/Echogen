package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayer
import cat.emir.echolib.command.getPlayers
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SpeedCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

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
        player.sendLangMessage("speed.fly.self", listOf("speed" to speed.toString()))

        return 1
    }

    fun flyspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.flySpeed = (speed / 10f)

            val speedTag = "speed" to speed.toString()
            sender.sendLangMessage("speed.fly.executor", listOf("player" to target.name, speedTag))
            target.sendLangMessage("speed.fly.target", listOf(speedTag))
        }

        return 1
    }

    fun walkspeed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        player.walkSpeed = (speed / 10f)
        player.sendLangMessage("speed.walk.self", listOf("speed" to speed.toString()))

        return 1
    }

    fun walkspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.walkSpeed = (speed / 10f)

            val speedTag = "speed" to speed.toString()
            sender.sendLangMessage("speed.walk.executor", listOf("player" to target.name, speedTag))
            target.sendLangMessage("speed.walk.target", listOf(speedTag))
        }

        return 1
    }

    fun allspeed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        player.flySpeed = (speed / 10f)
        player.walkSpeed = (speed / 10f)
        player.sendLangMessage("speed.all.self", listOf("speed" to speed.toString()))

        return 1
    }

    fun allspeedOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val speed = FloatArgumentType.getFloat(ctx, "speed")

        for (target in targets) {
            target.flySpeed = (speed / 10f)
            target.walkSpeed = (speed / 10f)

            val speedTag = "speed" to speed.toString()
            sender.sendLangMessage("speed.walk.executor", listOf("player" to target.name, speedTag))
            target.sendLangMessage("speed.walk.target", listOf(speedTag))
        }

        return 1
    }
}
