package cat.emir.echogen.commandlib

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack

class CommandLib {

    fun interface CommandAction<T> {
        fun accept(builder: T)
    }

    interface CommandNode {
        val node: com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, *>

        fun requires(block: (CommandSourceStack) -> Boolean) {
            node.requires(block)
        }

        fun executes(block: (CommandContext<CommandSourceStack>) -> Int) {
            node.executes(block)
        }

        fun <T> argument(
            name: String,
            type: ArgumentType<T>,
            setup: ArgumentBuilder<T>.(RequiredArgumentBuilder<CommandSourceStack, T>) -> Unit
        ) {
            val argumentBuilder = ArgumentBuilder(name, type)
            argumentBuilder.setup(argumentBuilder.node)
            node.then(argumentBuilder.node)
        }

        /**
         * for java compatibility
         */
        fun <T> argument(
            name: String,
            type: ArgumentType<T>,
            setup: CommandAction<ArgumentBuilder<T>>
        ) {
            val argumentBuilder = ArgumentBuilder(name, type)
            setup.accept(argumentBuilder)
            node.then(argumentBuilder.node)
        }

        fun subcommand(name: String, setup: CommandBuilder.(LiteralArgumentBuilder<CommandSourceStack>) -> Unit) {
            val commandBuilder = CommandBuilder(name)
            commandBuilder.setup(commandBuilder.node)
            node.then(commandBuilder.node)
        }

        /**
         * for java compatibility
         */
        fun subcommand(name: String, setup: CommandAction<CommandBuilder>) {
            val commandBuilder = CommandBuilder(name)
            setup.accept(commandBuilder)
            node.then(commandBuilder.node)
        }
    }

    class CommandBuilder(name: String) : CommandNode {
        override val node: LiteralArgumentBuilder<CommandSourceStack> = LiteralArgumentBuilder.literal(name)
    }

    class ArgumentBuilder<T>(name: String, type: ArgumentType<T>) : CommandNode {
        override val node: RequiredArgumentBuilder<CommandSourceStack, T> = RequiredArgumentBuilder.argument(name, type)
    }
}