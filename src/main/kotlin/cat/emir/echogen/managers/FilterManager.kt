package cat.emir.echogen.managers

import cat.emir.echogen.helpers.ConfigurateListHelper
import cat.emir.echogen.Echogen
import java.util.regex.Pattern

class FilterManager(val plugin: Echogen) {
    var filters = mutableListOf<FilterElement>()

    fun load() {
        filters.clear()

        val filterList = ConfigurateListHelper.getFilterList(plugin.config.rootNode.node("chat", "filter"))

        for (map in filterList) {
            val values = map.values.first()
            val regex = values["regex"]
            val type = FilterType.valueOf(values["type"]!!.uppercase())

            if (type == FilterType.COMMAND) {
                val command = values["command"]
                filters.add(FilterElement(type, Pattern.compile(regex!!, Pattern.CASE_INSENSITIVE), command))
                plugin.logger.info("Loaded a filter with regex: $regex type: $type command: $command")
            } else {
                filters.add(FilterElement(type, Pattern.compile(regex!!, Pattern.CASE_INSENSITIVE)))
                plugin.logger.info("Loaded a filter with regex: $regex type: $type")
            }
        }
    }

    data class FilterElement(
        val type: FilterType,
        val pattern: Pattern,
        val command: String? = null
    )

    enum class FilterType {
        CENSOR,
        BLOCK_MESSAGE,
        COMMAND
    }
}
