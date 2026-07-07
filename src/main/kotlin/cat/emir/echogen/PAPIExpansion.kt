package cat.emir.echogen

import ch.njol.skript.lang.function.Functions
import ch.njol.skript.variables.Variables
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

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

        } else if (params.startsWith("color_")) {
            return colorConversionPlaceholders(onlinePlayer, params)
        }

        return when (params.lowercase()) {
            "vanished" -> plugin.vanishManager.isVanished(onlinePlayer).toString()
            "prefix" -> plugin.prefixManager.getPlayerPrefixString(onlinePlayer) ?: ""
            else -> null
        }
    }

    // %echogen_sk_variable%
    // %echogen_sk_function_functionHere()%
    fun skriptPlaceholders(player: Player, params: String): String? {
        var variable = params.substring("sk_".length)
        if (variable.startsWith("function_")) {
            var function = variable.substring("function_".length)

            var paramList = mutableListOf<String>()

            val match = Regex("(.*)\\((.*)\\)").find(function)
            if (match != null) {
                function = match.groupValues[1]
                paramList = match.groupValues[2].split(Regex(" ?, ?")).toMutableList()
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
            if (returnValue.isNullOrEmpty()) return null

            return returnValue[0].toString()
        }

        var defaultValue: String? = null
        if (variable.contains("_?_")) {
            val split = variable.split("_?_")
            if (split.size != 2) return null

            variable = split[0]
            defaultValue = split[1]
        }

        variable = Regex("[{\\[](.*?)[}\\]]").replace(variable) {
            PlaceholderAPI.setPlaceholders(player, "%${it.groupValues[1]}%")
        }

        val value = Variables.getVariable(variable, null, false)
        if (value == null && defaultValue != null) return defaultValue
        return value.toString()
    }

    // %echogen_color_minimessage_text%
    fun colorConversionPlaceholders(player: Player, params: String): String? {
        val placeholder = params.substring("color_".length)
        if (placeholder.startsWith("minimessage_")) {
            var text = placeholder.substring("minimessage_".length)

            text = Regex("[{\\[](.*?)[}\\]]").replace(text) {
                PlaceholderAPI.setPlaceholders(player, "%${it.groupValues[1]}%")
            }

            return plugin.miniMessageUtils.convertColorToMiniMessage(text)
        }

        return null
    }
}
