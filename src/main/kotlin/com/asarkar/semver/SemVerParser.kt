package com.asarkar.semver

internal class SemVerParser {
    private var start = 0
    private var delimiters = setOf('.')

    fun parse(str: String): SemVer {
        val invalidNormalMessage = "Invalid normal version"
        if (str.isEmpty()) throw IllegalArgumentException(invalidNormalMessage)
        val major = parseNumericId(str, invalidNormalMessage)
        if (start >= str.length) throw IllegalArgumentException(invalidNormalMessage)
        val minor = parseNumericId(str, invalidNormalMessage)
        if (start >= str.length) throw IllegalArgumentException(invalidNormalMessage)
        delimiters = setOf('-', '+')
        val patch = parseNumericId(str, invalidNormalMessage)
        var preRelease: PreRelease? = null
        if (start <= str.length && str[start - 1] == '-') {
            delimiters = setOf('+')
            preRelease = PreRelease(parseIds(str, "Invalid pre-release version"))
        }
        var build: Build? = null
        if (start <= str.length && str[start - 1] == '+') {
            delimiters = emptySet()
            build = Build(parseIds(str, "Invalid build metadata", true))
        }

        return SemVer(
            Normal(major, minor, patch),
            preRelease,
            build
        )
    }

    private fun parseIds(str: String, errorMessage: String, allowLeadingZeroes: Boolean = false): List<Id> {
        return str.substring(start).takeWhile { !delimiters.contains(it) }
            .split("\\.".toRegex())
            .map { id ->
                try {
                    Id.parse(id, allowLeadingZeroes)
                        .also { start += id.length + 1 }
                } catch (ex: IllegalArgumentException) {
                    throw IllegalArgumentException(errorMessage, ex)
                }
            }
    }

    private fun parseNumericId(str: String, errorMessage: String, allowLeadingZeroes: Boolean = false): NumericId {
        val id = str.substring(start).takeWhile { !delimiters.contains(it) }
        try {
            return NumericId(id, allowLeadingZeroes)
                .also { start += id.length + 1 }
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(errorMessage, ex)
        }
    }
}
