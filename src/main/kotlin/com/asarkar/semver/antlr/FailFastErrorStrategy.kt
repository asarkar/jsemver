package com.asarkar.semver.antlr
//
// import org.antlr.v4.runtime.DefaultErrorStrategy
// import org.antlr.v4.runtime.InputMismatchException
// import org.antlr.v4.runtime.Parser
// import org.antlr.v4.runtime.RecognitionException
// import org.antlr.v4.runtime.Token
// import org.slf4j.LoggerFactory
//
// object FailFastErrorStrategy : DefaultErrorStrategy() {
//    private val log = LoggerFactory.getLogger(FailFastErrorStrategy::class.java)
//
//    /** Make sure we don't attempt to recover inline; if the parser successfully recovers, it won't throw an exception.
//     *  The rule function catch block catches RecognitionException and calls recover.
//     */
//    override fun recoverInline(recognizer: Parser): Token {
//        throw InputMismatchException(recognizer)
//    }
//
//    /** Instead of recovering from exception e, rethrow it.
//     */
//    override fun recover(recognizer: Parser, e: RecognitionException) {
//        val stack = recognizer.ruleInvocationStack
//        val segment = stack[stack.size - 2]
//                .split("(?=\\p{Upper})".toRegex())
//                .joinToString(separator = " ") { it.toLowerCase() }
//
//        throw IllegalArgumentException("Invalid $segment in: ${recognizer.tokenStream.text}")
//    }
//
//    /** Make sure we don't attempt to recover from problems in subrules. */
//    override fun sync(recognizer: Parser) {}
// }
