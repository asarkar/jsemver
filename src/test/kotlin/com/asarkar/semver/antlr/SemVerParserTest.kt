package com.asarkar.semver.antlr

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class SemVerParserTest {
    @ParameterizedTest
    @ValueSource(strings = ["1.0.0-alpha+001", "1.0"])
    fun testParse(v: String) {
        val parser = newParser(v)
        assertThat(ParseTreeNode(parser.semVer()).toString())
            .isEqualTo(File(javaClass.getResource("/parser/$v.txt").toURI()).readText())
    }

    private fun newParser(input: String): SemVerParser {
        val chars = CharStreams.fromString(input)
        val lexer = SemVerLexer(chars)
        val tokens = CommonTokenStream(lexer)
        return SemVerParser(tokens)
    }
}
