package com.asarkar.semver.antlr

import com.asarkar.semver.SemVerParser
import com.asarkar.semver.SemVerParserType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AntlrSemVerParserTest {
    @ParameterizedTest
    @ValueSource(strings = ["1", "1.", "1.0", "1.0.", "1. a"])
    fun testNormalVersionParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }

    @Test
    fun testInvalidNormalVersionSymbol() {
        val str = "1.0.&"
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0-01", "1.0.0-"])
    fun testPreReleaseVersionParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }

    @Test
    fun testInvalidPreReleaseSymbol() {
        val str = "1.0.0-a&^"
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0+"])
    fun testBuildMetadataParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }

    @Test
    fun testInvalidBuildMetadataSymbol() {
        val str = "1.0.0+a&^"
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.ANTLR).parseStr(str)
        }
            .withMessageStartingWith("Invalid input: [$str] - violation at line")
    }
}
