package cat.emir.echogen.utils

object TypeParser {
    enum class ValueType {
        STRING,
        STRING_WITH_QUOTES,
        BOOLEAN,
        DOUBLE
    }

    fun getValueType(value: String): ValueType {
        return if (value.matches(Regex("""[0-9]+(?:\.[0-9]+)?"""))) {
            ValueType.DOUBLE
        } else if (value.lowercase() == "true" || value.lowercase() == "false"
            || value.lowercase() == "yes"  || value.lowercase() == "no") {
            ValueType.BOOLEAN
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            ValueType.STRING_WITH_QUOTES
        } else if (value.startsWith("'") && value.endsWith("'")) {
            ValueType.STRING_WITH_QUOTES
        } else {
            ValueType.STRING
        }
    }

    enum class Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER,
        LESS,
        GREATER_OR_EQUALS,
        LESS_OR_EQUALS,
    }

    private fun convertString(string: String, type: ValueType): String {
        return when (type) {
            ValueType.BOOLEAN if string == "yes" -> "true"
            ValueType.BOOLEAN if string == "no" -> "false"
            ValueType.STRING_WITH_QUOTES -> string.substring(1, string.length - 1)
            else -> string
        }
    }

    fun compare(first: String, second: String, operator: Operator): Boolean {
        val firstType = getValueType(first)
        val secondType = getValueType(second)

        val firstString = convertString(first, firstType)
        val secondString = convertString(second, secondType)

        val type = if (firstType != secondType) ValueType.STRING else firstType

        return when (type) {
            ValueType.DOUBLE -> {
                val firstDouble = firstString.toDouble()
                val secondDouble = secondString.toDouble()

                when (operator) {
                    Operator.EQUALS -> firstDouble == secondDouble
                    Operator.NOT_EQUALS -> firstDouble != secondDouble
                    Operator.GREATER -> firstDouble > secondDouble
                    Operator.GREATER_OR_EQUALS -> firstDouble >= secondDouble
                    Operator.LESS -> firstDouble < secondDouble
                    Operator.LESS_OR_EQUALS -> firstDouble <= secondDouble
                }
            }
            ValueType.BOOLEAN -> {
                val firstBoolean = firstString.toBoolean()
                val secondBoolean = secondString.toBoolean()

                when (operator) {
                    Operator.EQUALS -> firstBoolean == secondBoolean
                    Operator.NOT_EQUALS -> firstBoolean != secondBoolean
                    Operator.GREATER -> firstBoolean > secondBoolean
                    Operator.GREATER_OR_EQUALS -> firstBoolean >= secondBoolean
                    Operator.LESS -> firstBoolean < secondBoolean
                    Operator.LESS_OR_EQUALS -> firstBoolean <= secondBoolean
                }
            }
            ValueType.STRING_WITH_QUOTES, ValueType.STRING -> when (operator) {
                Operator.EQUALS -> firstString == secondString
                Operator.NOT_EQUALS -> firstString != secondString
                Operator.GREATER -> firstString >= secondString
                Operator.GREATER_OR_EQUALS -> firstString <= secondString
                Operator.LESS -> firstString < secondString
                Operator.LESS_OR_EQUALS -> firstString <= secondString
            }
        }
    }
}