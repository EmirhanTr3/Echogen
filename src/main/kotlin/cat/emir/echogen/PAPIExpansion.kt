package cat.emir.echogen

import java.util.regex.Pattern

import ch.njol.skript.lang.function.Functions
import ch.njol.skript.variables.Variables
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import java.util.Objects

class PAPIExpansion(val plugin: Echogen) : PlaceholderExpansion() {
    override fun getAuthor(): String {
        return "EmirhanTr3"
    }

    override fun getIdentifier(): String {
        return "echogen"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (player == null || !player.isOnline)
            return null

        val onlinePlayer = plugin.server.getPlayer(player.uniqueId)!!

        if (params.startsWith("sk_")) {
            if (!plugin.isSkriptEnabled) return null
            return skriptPlaceholders(onlinePlayer, params)
        }

        return when (params.lowercase()) {
            "vanished" -> plugin.vanishManager.isVanished(onlinePlayer).toString()
            "prefix" -> Objects.requireNonNullElse(
                    plugin.prefixManager.getPlayerPrefixString(onlinePlayer), "")
            else -> null
        }
    }

    fun skriptPlaceholders(player: Player, params: String): String? {
        var variable = params.substring("sk_".length)
        if (variable.startsWith("function_")) {
            var function = variable.substring("function_".length)

            val matcher = Pattern.compile("(.*)\\((.*)\\)").matcher(function)
            var paramList = mutableListOf<String>()

            if (matcher.find()) {
                function = matcher.group(1)
                paramList = matcher.group(2).split(Regex(" ?, ?")).toMutableList()
            }

            val func = Functions.getGlobalFunction(function) ?: return null

            val parsedFuncParams = mutableListOf<Any?>()

            for ((i, param) in paramList.withIndex()) {
                val parameter = func.parameters[i]
                val type = parameter.getType().getC()

                if (param == "player") {
                    parsedFuncParams.add(player)
                    continue
                }

                val value = PlaceholderAPI.setPlaceholders(player, "%$param%")
                val output = when {
                    type == Long::class.java -> value.toLongOrNull()
                    Number::class.java.isAssignableFrom(type) -> value.toDoubleOrNull()
                    else -> value
                }

                parsedFuncParams.add(output)
            }

            val finalFuncParams = arrayOf(parsedFuncParams.toTypedArray())
            val returnValue = func.execute(finalFuncParams)
            if (returnValue == null || returnValue.isEmpty()) return null

            return returnValue[0].toString()
        }

        var defaultValue: String? = null
        if (variable.contains("_?_")) {
            val split = variable.split("_?_")
            if (split.size != 2) return null

            variable = split[0]
            defaultValue = split[1]
        }

        val matcher = Pattern.compile("[{\\[](.*?)[}\\]]").matcher(variable)

        while (matcher.find()) {
            val match = matcher.group(0)
            val placeholder = matcher.group(1)
            variable = variable.replace(match, PlaceholderAPI.setPlaceholders(player, "%$placeholder%"))
        }

        val value = Variables.getVariable(variable, null, false)
        if (value == null && defaultValue != null) return defaultValue
        return value.toString()
    }
}
