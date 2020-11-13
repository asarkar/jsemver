package com.asarkar.semver

internal class SimpleSemVerParser internal constructor() : SemVerParser {
    private var start: Int = 0
    private var delimiters = setOf('.')

    override fun parseStr(input: String): SemVer {
        try {
            return parseStrInternal(input)
        } finally {
            start = 0
            delimiters = setOf('.')
        }
    }

    private fun parseStrInternal(input: String): SemVer {
        val invalidNormalMessage = "Invalid input: [$input] - cannot parse normal version"
        if (input.isEmpty()) throw IllegalArgumentException(invalidNormalMessage)
        val major = parseNumericId(input, invalidNormalMessage)
        if (start >= input.length) throw IllegalArgumentException(invalidNormalMessage)
        val minor = parseNumericId(input, invalidNormalMessage)
        if (start >= input.length) throw IllegalArgumentException(invalidNormalMessage)
        delimiters = setOf('-', '+')
        val patch = parseNumericId(input, invalidNormalMessage)
        var preReleaseVersion: PreReleaseVersion? = null
        if (start <= input.length && input[start - 1] == '-') {
            delimiters = setOf('+')
            preReleaseVersion = PreReleaseVersion(parseIds(input, "Invalid input: [$input] - cannot parse pre release version"))
        }
        var buildMetadata: BuildMetadata? = null
        if (start <= input.length && input[start - 1] == '+') {
            delimiters = emptySet()
            buildMetadata = BuildMetadata(parseIds(input, "Invalid input: [$input] - cannot parse build metadata", true))
        }

        return SemVer(
            NormalVersion(major, minor, patch),
            preReleaseVersion,
            buildMetadata
        )
    }

    private fun parseIds(input: String, errorMessage: String, allowLeadingZeroes: Boolean = false): List<Id> {
        return input.substring(start).takeWhile { !delimiters.contains(it) }
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

    private fun parseNumericId(input: String, errorMessage: String, allowLeadingZeroes: Boolean = false): NumericId {
        val id = input.substring(start).takeWhile { !delimiters.contains(it) }
        try {
            return NumericId(id, allowLeadingZeroes)
                .also { start += id.length + 1 }
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(errorMessage, ex)
        }
    }
}
