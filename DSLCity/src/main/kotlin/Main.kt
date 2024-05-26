package org.example

import org.json.JSONObject
import java.io.File
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.typeOf


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


    //File(args[1]).outputStream().write(Parser((Scanner(ForForeachFFFAutomaton, File(args[0]).inputStream()))).parse().toString().toByteArray())
    Parser((Scanner(ForForeachFFFAutomaton, File(args[0]).inputStream()))).parse()?.toGEOJson(File(args[1]).outputStream())
    println(vars)

//val json: JSONObject = JSONObject(File(args[1]).readText(Charsets.UTF_8)) // Convert text to object
    //File(args[1]).writeText(json.toString(4))
}