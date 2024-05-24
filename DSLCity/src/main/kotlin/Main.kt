package org.example

import java.io.File
import java.io.OutputStream

fun printTokens(scanner: Scanner, output: OutputStream) {
    val writer = output.writer(Charsets.UTF_8)

    var token = scanner.getToken()
    while (token.symbol != Symbol.EOF) {
        writer.append("${name(token.symbol)}(\"${token.lexeme}\") ") // The output ends with a space!
        token = scanner.getToken()
    }
    writer.appendLine()
    writer.flush()
}

fun main(args: Array<String>) {

    printTokens(Scanner(ForForeachFFFAutomaton, File(args[0]).inputStream()), System.out)
}