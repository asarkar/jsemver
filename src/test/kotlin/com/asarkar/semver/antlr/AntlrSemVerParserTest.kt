package com.asarkar.semver.antlr

import com.asarkar.semver.SemVerParser
import com.asarkar.semver.SemVerParserType
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AntlrSemVerParserTest {
    @ParameterizedTest
    @MethodSource("versionProvider")
    fun testParseError(v: String, position: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(v)
        }
            .withMessage("Invalid input: [$v] - violation at line $position")
    }

    companion object {
        @JvmStatic
        fun versionProvider(): List<Arguments> {
            return listOf(
                Arguments.of("1", "1:1"),
                Arguments.of("1.", "1:2"),
                Arguments.of("1.0", "1:3"),
                Arguments.of("1.0.", "1:4"),
                Arguments.of("1. a", "1:2"),
                Arguments.of("1.0.&", "1:4"),
                Arguments.of("1.0.0-01", "1:8"),
                Arguments.of("1.0.0-", "1:6"),
                Arguments.of("1.0.0-a&^", "1:7"),
                Arguments.of("1.0.0+", "1:6"),
                Arguments.of("1.0.0+a&^", "1:7")
            )
        }
    }
}
