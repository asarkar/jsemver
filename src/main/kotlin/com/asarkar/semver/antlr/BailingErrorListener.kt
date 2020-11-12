package com.asarkar.semver.antlr

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CodePointCharStream
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.Interval
import org.slf4j.LoggerFactory

object BailingErrorListener : BaseErrorListener() {
    private val log = LoggerFactory.getLogger(BailingErrorListener::class.java)

    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        val input = if (recognizer is Parser) {
            val stack = recognizer.ruleInvocationStack
            val rule = stack[stack.size - 2]
                .split("(?=\\p{Upper})".toRegex())
                .joinToString(separator = " ") { it.toLowerCase() }
            log.error("Failed to parse rule: $rule", e)
            rebuildInput(recognizer)
        } else {
            log.error(msg, e)
            rebuildInput(recognizer as Lexer)
        }
        throw IllegalArgumentException("Invalid input: [$input] - violation at line $line:$charPositionInLine")
    }

    private fun rebuildInput(parser: Parser): String = parser.tokenStream.text
    private fun rebuildInput(lexer: Lexer): String {
        val inputStream = lexer.inputStream as CodePointCharStream
        val end = inputStream.size()
        return inputStream.getText(Interval(0, end))
    }
}
