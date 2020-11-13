package com.asarkar.semver.antlr

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class SemVerLexerTest {
    @ParameterizedTest
    @MethodSource("versionsProvider")
    fun testLexer(v: String, tokens: List<String>) {
        val lexer = SemVerLexer(CharStreams.fromString(v))
        val tokenStream = CommonTokenStream(lexer)
        tokenStream.fill()
        val actual = tokenStream.tokens
            .map { "${lexer.vocabulary.getSymbolicName(it.type)}:${it.text}" }
        assertThat(actual).containsExactlyElementsOf(tokens)
    }

    companion object {
        @JvmStatic
        fun versionsProvider(): List<Arguments> {
            return listOf(
                arguments(
                    "1.0.0-alpha+001",
                    listOf(
                        "POSITIVE_DIGIT:1", "DOT:.", "ZERO:0", "DOT:.", "ZERO:0",
                        "HYPHEN:-",
                        "LETTER:a", "LETTER:l", "LETTER:p", "LETTER:h", "LETTER:a",
                        "PLUS:+",
                        "ZERO:0", "ZERO:0", "POSITIVE_DIGIT:1",
                        "EOF:<EOF>"
                    )
                ),
                arguments(
                    "1.0",
                    listOf(
                        "POSITIVE_DIGIT:1", "DOT:.", "ZERO:0",
                        "EOF:<EOF>"
                    )
                ),
                arguments(
                    "1.0.0-beta+exp.sha.5114f85",
                    listOf(
                        "POSITIVE_DIGIT:1", "DOT:.", "ZERO:0", "DOT:.", "ZERO:0",
                        "HYPHEN:-",
                        "LETTER:b", "LETTER:e", "LETTER:t", "LETTER:a",
                        "PLUS:+",
                        "LETTER:e", "LETTER:x", "LETTER:p",
                        "DOT:.",
                        "LETTER:s", "LETTER:h", "LETTER:a",
                        "DOT:.",
                        "POSITIVE_DIGIT:5", "POSITIVE_DIGIT:1", "POSITIVE_DIGIT:1", "POSITIVE_DIGIT:4",
                        "LETTER:f", "POSITIVE_DIGIT:8", "POSITIVE_DIGIT:5",
                        "EOF:<EOF>"
                    )
                )
            )
        }
    }
}
