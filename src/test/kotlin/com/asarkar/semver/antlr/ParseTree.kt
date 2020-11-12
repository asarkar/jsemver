package com.asarkar.semver.antlr

import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode

interface ParseTreeElement

class ParseTreeLeaf(private val node: TerminalNode, private val indentation: String) : ParseTreeElement {
    override fun toString(): String {
        val lexer = node.symbol.tokenSource as Lexer
        val name = lexer.vocabulary.getSymbolicName(node.symbol.type)
        return "${indentation}$name:\"${node.text}\"\n"
    }
}

class ParseTreeNode(val node: ParserRuleContext, private val indentation: String = "") : ParseTreeElement {
    private val name = node::class.simpleName!!.removeSuffix("Context")
    private val children = node.children
        .map {
            when (it) {
                is ParserRuleContext -> ParseTreeNode(it, "$indentation\t")
                is TerminalNode -> ParseTreeLeaf(it, "$indentation\t")
                else -> throw IllegalArgumentException("Don't know what to do with: ${it::class.qualifiedName}")
            }
        }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.appendLine("$indentation$name")
        children.forEach { sb.append(it) }
        return sb.toString()
    }
}
