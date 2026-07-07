package cat.emir.echogen.utils

import org.bukkit.Bukkit

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import cat.emir.echogen.Echogen

class MiniMessageUtils(val plugin: Echogen) {
    companion object {
        val colorAndFormatMap = mapOf(
            '0' to "black",      '1' to "dark_blue",    '2' to "dark_green",    '3' to "dark_aqua",
            '4' to "dark_red",   '5' to "dark_purple",  '6' to "gold",          '7' to "gray",
            '8' to "dark_gray",  '9' to "blue",         'a' to "green",         'b' to "aqua",
            'c' to "red",        'd' to "light_purple", 'e' to "yellow",        'f' to "white",
            'k' to "obfuscated", 'l' to "bold",         'm' to "strikethrough", 'n' to "underlined",
            'o' to "italic",     'r' to "reset"
        )
    }

    var ticks = 0L
    val animatedGradientStep = mutableMapOf<String, Double>()

    fun load() {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable { ticks++ }, 0, 1)
    }

    // <animatedgradient:id:color1:color...:tick:step>
    fun animatedGradientTag(): TagResolver {
        return TagResolver.resolver("animatedgradient") { args, ctx ->
            val id = args.pop().value()
            val colors = mutableListOf<TextColor>()
            var tick: Long? = null
            var step: Double? = null

            while (args.hasNext()) {
                val arg = args.pop()
                val value = arg.value()
                val color = resolveColorOrNull(value)
                if (color != null) {
                    colors.add(color)
                } else {
                    tick = value.toLongOrNull()
                    step = args.pop().value().toDoubleOrNull()
                    break
                }
            }

            if (tick == null || step == null)
                Tag.selfClosingInserting(Component.text("invalid_animation_gradient"))

            if (animatedGradientStep.containsKey(id)) {
                if (ticks % tick!! == 0L) {
                    animatedGradientStep[id] = animatedGradientStep[id]!! + step!!
                    if (animatedGradientStep[id]!! > 1)
                        animatedGradientStep[id] = (-1).toDouble()
                    if (animatedGradientStep[id]!! < -1)
                        animatedGradientStep[id] = 1.toDouble()
                }
            } else {
                animatedGradientStep[id] = 0.toDouble()
            }

            Tag.preProcessParsed(
                "<gradient:${colors.joinToString(":") { it.toString() }}:${animatedGradientStep[id]}>")
        }
    }

    fun resolveColorOrNull(colorName: String): TextColor? {
        if (colorName[0] == TextColor.HEX_CHARACTER)
            return TextColor.fromHexString(colorName)
        return NamedTextColor.NAMES.value(colorName)
    }

    fun convertColorToMiniMessage(text: String): String {
        var result = text

        val vanillaHexRegex = Regex("[&§]x([&§][0-9a-fA-F]){6}")
        result = vanillaHexRegex.replace(result) { match ->
            val hex = match.value.replace(Regex("[&§]x"), "").replace(Regex("[&§]"), "")
            "<#$hex>"
        }

        val hexRegex = Regex("[&§]#([0-9a-fA-F]{6})")
        result = hexRegex.replace(result) { match ->
            "<#${match.groupValues[1]}>"
        }

        val regex = Regex("[&§]([0-9a-fk-or])", RegexOption.IGNORE_CASE)
        result = regex.replace(result) { match ->
            val code = match.groupValues[1].lowercase()[0]
            val tag = colorAndFormatMap[code]
            if (tag != null) "<$tag>" else match.value
        }

        return result
    }
}
