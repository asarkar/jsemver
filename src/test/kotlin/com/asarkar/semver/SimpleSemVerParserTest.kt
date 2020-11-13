package com.asarkar.semver

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SimpleSemVerParserTest {
    @ParameterizedTest
    @ValueSource(strings = ["1", "1.", "1.0", "1.0.", "1. a", "1.0.&"])
    fun testNormalVersionParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.SIMPLE).parseStr(str)
        }
            .withMessage("Invalid input: [$str] - cannot parse normal version")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0-01", "1.0.0-", "1.0.0-a&^"])
    fun testPreReleaseParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.SIMPLE).parseStr(str)
        }
            .withMessage("Invalid input: [$str] - cannot parse pre release version")
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0+", "1.0.0+a&^"])
    fun testBuildMetadataParseError(str: String) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException::class.java).isThrownBy {
            SemVerParser.getInstance(SemVerParserType.SIMPLE).parseStr(str)
        }
            .withMessage("Invalid input: [$str] - cannot parse build metadata")
    }
}
