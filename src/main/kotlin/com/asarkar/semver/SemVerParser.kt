package com.asarkar.semver

import com.asarkar.semver.antlr.AntlrSemVerParser

/**
 * SemVer parser types.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
enum class SemVerParserType {
    SIMPLE, ANTLR
}

/**
 * SemVer parser.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
interface SemVerParser {
    /**
     * Parses [input] to produce a [SemVer].
     * @throws IllegalArgumentException if the input couldn't be parsed.
     */
    fun parseStr(input: String): SemVer

    companion object {
        /**
         * Returns a [SemVerParser] corresponding to [type].
         */
        fun getInstance(type: SemVerParserType): SemVerParser {
            return when (type) {
                SemVerParserType.SIMPLE -> SimpleSemVerParser()
                SemVerParserType.ANTLR -> AntlrSemVerParser()
            }
        }
    }
}
