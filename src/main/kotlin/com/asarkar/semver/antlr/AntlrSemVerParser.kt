package com.asarkar.semver.antlr

import com.asarkar.semver.SemVer
import com.asarkar.semver.SemVerParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class AntlrSemVerParser internal constructor() : SemVerParser {
    override fun parseStr(input: String): SemVer {
        val chars = CharStreams.fromString(input)
//        val lexer = FailFastSemVerLexer(chars)
        val lexer = SemVerLexer(chars)
        lexer.removeErrorListeners()
        lexer.addErrorListener(BailingErrorListener)
        val tokens = CommonTokenStream(lexer)
        val parser = SemVerParser(tokens)
        parser.removeErrorListeners() // remove ConsoleErrorListener
        parser.addErrorListener(BailingErrorListener)
//        parser.errorHandler = FailFastErrorStrategy
        return SemVerVisitorImpl().visit(parser.semVer())
    }
}
