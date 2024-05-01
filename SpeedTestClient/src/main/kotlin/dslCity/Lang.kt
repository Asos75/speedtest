package dslCity

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import kotlin.math.floor
import kotlin.math.pow

// WARNING: This code has nothing to do with the task at hand. Feel free to delete it.

fun echo(prefix: String, input: InputStream, output: OutputStream) {
    val buffer = mutableListOf<Byte>()

    while (true) {
        val symbol = input.read()
        if (symbol == -1) break
        buffer.add(symbol.toByte())

    }
    output.write(prefix.toByteArray() + buffer.toByteArray())
}

const val ERROR_STATE = 0

enum class CharTypes {
    LETTERSUPERCASE,
    LETTERSLOWERCASE,
    NUMBERS
}

enum class Symbol {
    EOF,
    REAL,
    VARIABLE,
    PLUS,
    MINUS,
    TIMES,
    DIVIDES,
    INTEGERDIVIDES,
    POW,
    LPAREN,
    RPAREN,
    SKIP,
    ASSIGN,
    DEFINE,
    TERM,
    FOR,
    TO,
    BEGIN,
    END,
    PRINT,
    VAR
}

const val EOF = -1
const val NEWLINE = '\n'.code

interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Symbol
    val startState: Int
    val finalStates: Set<Int>
}

object ForForeachFFFAutomaton : DFA {
    override val states = (1..32).toSet()
    override val alphabet = 0..255
    override val startState = 1
    override val finalStates = setOf(
        2,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32
    )

    private val numberOfStates = states.max() + 1 // plus the ERROR_STATE
    private val numberOfCodes = alphabet.max() + 1 // plus the EOF
    private val transitions = Array(numberOfStates) { IntArray(numberOfCodes) }
    private val values = Array(numberOfStates) { Symbol.SKIP }

    private fun setTransition(from: Int, chr: Char, to: Int) {
        transitions[from][chr.code + 1] = to // + 1 because EOF is -1 and the array starts at 0
    }

    private fun setTransition(from: Int, code: Int, to: Int) {
        transitions[from][code + 1] = to
    }

    private fun setSymbol(state: Int, symbol: Symbol) {
        values[state] = symbol
    }

    override fun next(state: Int, code: Int): Int {
        assert(states.contains(state))
        assert(alphabet.contains(code))
        return transitions[state][code + 1]
    }

    override fun symbol(state: Int): Symbol {
        assert(states.contains(state))
        return values[state]
    }

    private fun setTransition(from: Int, types: CharTypes, to: Int) {
        when (types) {
            CharTypes.LETTERSUPERCASE -> {
                for (i in 65..90) {
                    setTransition(from, i, to)
                }
            }

            CharTypes.LETTERSLOWERCASE -> {
                for (i in 97..122) {
                    setTransition(from, i, to)
                }
            }

            CharTypes.NUMBERS -> {
                for (i in 48..57) {
                    setTransition(from, i, to)
                }
            }
        }

    }

    init {
        setTransition(startState, EOF, 32)
        //region REAL:
        setTransition(startState, CharTypes.NUMBERS, 2)
        setTransition(2, CharTypes.NUMBERS, 2)
        setTransition(2, '.', 3)
        setTransition(3, CharTypes.NUMBERS, 4)
        setTransition(4, CharTypes.NUMBERS, 4)
//endregion

//region VARIABLES:
        setTransition(startState, CharTypes.LETTERSUPERCASE, 5)
        setTransition(startState, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(5, CharTypes.LETTERSUPERCASE, 5)
        setTransition(5, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(5, CharTypes.NUMBERS, 6)
        setTransition(6, CharTypes.NUMBERS, 6)
//endregion

//region OPERATORS:
        setTransition(startState, '+', 7)
        setTransition(startState, '-', 8)
        setTransition(startState, '*', 9)
        setTransition(startState, '/', 10)
        setTransition(10, '/', 11)
        setTransition(startState, '^', 12)
        setTransition(startState, '(', 13)
        setTransition(startState, ')', 14)
//endregion

//region SKIPS:
        setTransition(startState, ' ', 15)
        setTransition(startState, '\t', 15)
        setTransition(startState, '\n', 15)
        setTransition(startState, '\r', 15)
        setTransition(15, ' ', 15)
        setTransition(15, '\t', 15)
        setTransition(15, '\n', 15)
        setTransition(15, '\r', 15)
//endregion

//region SYMBOLS
        //region REAL:
        setTransition(startState, CharTypes.NUMBERS, 2)
        setTransition(2, CharTypes.NUMBERS, 2)
        setTransition(2, '.', 3)
        setTransition(3, CharTypes.NUMBERS, 4)
        setTransition(4, CharTypes.NUMBERS, 4)
//endregion

//region VARIABLES:
        setTransition(startState, CharTypes.LETTERSUPERCASE, 5)
        setTransition(startState, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(5, CharTypes.LETTERSUPERCASE, 5)
        setTransition(5, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(5, CharTypes.NUMBERS, 6)
        setTransition(6, CharTypes.NUMBERS, 6)
//endregion

//region OPERATORS:
        setTransition(startState, '+', 7)
        setTransition(startState, '-', 8)
        setTransition(startState, '*', 9)
        setTransition(startState, '/', 10)
        setTransition(10, '/', 11)
        setTransition(startState, '^', 12)
        setTransition(startState, '(', 13)
        setTransition(startState, ')', 14)
        setTransition(startState, '=', 16)
        setTransition(startState, ';', 17)
        setTransition(startState, '{', 18)
        setTransition(startState, '}', 19)
        setTransition(startState, ',', 20)
//endregion

//region SKIPS:
        setTransition(startState, ' ', 15)
        setTransition(startState, '\t', 15)
        setTransition(startState, '\n', 15)
        setTransition(startState, '\r', 15)
        setTransition(15, ' ', 15)
        setTransition(15, '\t', 15)
        setTransition(15, '\n', 15)
        setTransition(15, '\r', 15)
//endregion

//region RESERVED WORDS
        setTransition(startState, 'p', 21)
        setTransition(21, CharTypes.LETTERSUPERCASE, 5)
        setTransition(21, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(21, CharTypes.NUMBERS, 6)
        setTransition(21, 'r', 22)
        setTransition(22, CharTypes.LETTERSUPERCASE, 5)
        setTransition(22, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(22, CharTypes.NUMBERS, 6)
        setTransition(22, 'i', 23)
        setTransition(23, CharTypes.LETTERSUPERCASE, 5)
        setTransition(23, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(23, CharTypes.NUMBERS, 6)
        setTransition(23, 'n', 24)
        setTransition(24, CharTypes.LETTERSUPERCASE, 5)
        setTransition(24, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(24, CharTypes.NUMBERS, 6)
        setTransition(24, 't', 25)
        setTransition(25, CharTypes.LETTERSUPERCASE, 5)
        setTransition(25, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(25, CharTypes.NUMBERS, 6)

        setTransition(startState, 'f', 26)
        setTransition(26, CharTypes.LETTERSUPERCASE, 5)
        setTransition(26, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(26, CharTypes.NUMBERS, 6)
        setTransition(26, 'o', 27)
        setTransition(27, CharTypes.LETTERSUPERCASE, 5)
        setTransition(27, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(27, CharTypes.NUMBERS, 6)
        setTransition(27, 'r', 28)
        setTransition(28, CharTypes.LETTERSUPERCASE, 5)
        setTransition(28, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(28, CharTypes.NUMBERS, 6)

        setTransition(startState, 'v', 29)
        setTransition(29, CharTypes.LETTERSUPERCASE, 5)
        setTransition(29, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(29, CharTypes.NUMBERS, 6)
        setTransition(29, 'a', 30)
        setTransition(30, CharTypes.LETTERSUPERCASE, 5)
        setTransition(30, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(30, CharTypes.NUMBERS, 6)
        setTransition(30, 'r', 31)
        setTransition(31, CharTypes.LETTERSUPERCASE, 5)
        setTransition(31, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(31, CharTypes.NUMBERS, 6)
//endregion

//region SYMBOLS
        setSymbol(2, Symbol.REAL)
        setSymbol(4, Symbol.REAL)
        setSymbol(5, Symbol.VARIABLE)
        setSymbol(6, Symbol.VARIABLE)
        setSymbol(7, Symbol.PLUS)
        setSymbol(8, Symbol.MINUS)
        setSymbol(9, Symbol.TIMES)
        setSymbol(10, Symbol.DIVIDES)
        setSymbol(11, Symbol.INTEGERDIVIDES)
        setSymbol(12, Symbol.POW)
        setSymbol(13, Symbol.LPAREN)
        setSymbol(14, Symbol.RPAREN)
        setSymbol(15, Symbol.SKIP)
        setSymbol(16, Symbol.ASSIGN)
        setSymbol(17, Symbol.TERM)
        setSymbol(18, Symbol.BEGIN)
        setSymbol(19, Symbol.END)
        setSymbol(20, Symbol.TO)
        setSymbol(21, Symbol.VARIABLE)
        setSymbol(22, Symbol.VARIABLE)
        setSymbol(23, Symbol.VARIABLE)
        setSymbol(24, Symbol.VARIABLE)
        setSymbol(25, Symbol.PRINT)
        setSymbol(26, Symbol.VARIABLE)
        setSymbol(27, Symbol.VARIABLE)
        setSymbol(28, Symbol.FOR)
        setSymbol(29, Symbol.VARIABLE)
        setSymbol(30, Symbol.VARIABLE)
        setSymbol(31, Symbol.VAR)
        setSymbol(32, Symbol.EOF)
//endregion
    }

}


data class Token(val symbol: Symbol, val lexeme: String, val startRow: Int, val startColumn: Int)

class Scanner(private val automaton: DFA, private val stream: InputStream) {
    private var last: Int? = null
    private var row = 1
    private var column = 1

    private fun updatePosition(code: Int) {
        if (code == NEWLINE) {
            row += 1
            column = 1
        } else {
            column += 1
        }
    }

    fun getToken(): Token {
        val startRow = row
        val startColumn = column
        val buffer = mutableListOf<Char>()

        var code = last ?: stream.read()
        var state = automaton.startState
        while (true) {
            val nextState = automaton.next(state, code)
            if (nextState == ERROR_STATE) break // Longest match

            state = nextState
            updatePosition(code)
            buffer.add(code.toChar())
            code = stream.read()
        }
        last = code // The code following the current lexeme is the first code of the next lexeme

        if (automaton.finalStates.contains(state)) {
            val symbol = automaton.symbol(state)
            return if (symbol == Symbol.SKIP) {
                getToken()
            } else {
                val lexeme = String(buffer.toCharArray())
                Token(symbol, lexeme, startRow, startColumn)
            }
        } else {
            throw Error("Invalid pattern at ${row}:${column}")
        }
    }
}


fun name(value: Symbol): String {
    return when (value) {
        Symbol.REAL -> "real"
        Symbol.VARIABLE -> "variable"
        Symbol.PLUS -> "plus"
        Symbol.MINUS -> "minus"
        Symbol.TIMES -> "times"
        Symbol.DIVIDES -> "divides"
        Symbol.INTEGERDIVIDES -> "integer-divides"
        Symbol.POW -> "pow"
        Symbol.LPAREN -> "lparen"
        Symbol.RPAREN -> "rparen"
        Symbol.ASSIGN -> "assign"
        Symbol.TERM -> "term"
        Symbol.TO -> "to"
        Symbol.BEGIN -> "begin"
        Symbol.END -> "end"
        Symbol.DEFINE -> "define"
        Symbol.FOR -> "for"
        Symbol.PRINT -> "print"
        Symbol.VAR -> "define"
        Symbol.EOF -> "EOF"
        else -> throw Error("INVALID")
    }
}

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


class Parser(
    private val lex: Scanner,
    private var currentToken: Token = lex.getToken()
) {

    private fun expr(): Sent {
        val result: Sent? = statementList()
        if(result != null) return result
        throw Error("Invalid")
    }

    private fun statementList(): Sent? {
        if (currentToken.symbol == Symbol.EOF || currentToken.symbol == Symbol.END) {
            return null
        }
        return Sent(statement(), statementList())
    }

    private fun statement(): Sentence {
        return when (currentToken.symbol) {
            Symbol.PRINT -> {
                currentToken = lex.getToken()
                terminator(print())
            }

            Symbol.VAR -> {
                currentToken = lex.getToken()
                terminator(define())
            }

            Symbol.FOR -> {
                currentToken = lex.getToken()
                forLoop()
            }

            Symbol.VARIABLE -> {
                val variable = currentToken.lexeme
                currentToken = lex.getToken()
                terminator(reassign(variable) as Sentence)
            }

            else -> throw Error("Invalid")
        }
    }

    private fun terminator(expr: Sentence): Sentence {
        if (currentToken.symbol == Symbol.TERM) {
            currentToken = lex.getToken()
            return expr
        }
        throw Error("Invalid")
    }

    private fun additive(): Expr{
        val result: Expr = multiplicative()
        return additive2(result)
    }

    private fun additive2(inVal: Expr): Expr{
        if(currentToken.symbol == Symbol.PLUS){
            currentToken = lex.getToken()
            val plus: Expr = Plus(inVal, multiplicative())
            return additive2(plus)
        }
        else if(currentToken.symbol == Symbol.MINUS){
            currentToken = lex.getToken()
            val minus: Expr = Minus(inVal, multiplicative())
            return additive2(minus)
        }
        return inVal
    }

    private fun multiplicative(): Expr{
        val result: Expr = exponential()
        return  multiplicative2(result)
    }

    private fun multiplicative2(inVal: Expr): Expr{
        if(currentToken.symbol == Symbol.TIMES){
            currentToken = lex.getToken()
            val times: Expr = Times(inVal, exponential())
            return multiplicative2(times)
        }
        else if(currentToken.symbol == Symbol.DIVIDES){
            currentToken = lex.getToken()
            val divides: Expr = Divides(inVal, exponential())
            return multiplicative2(multiplicative2(divides))
        }
        else if(currentToken.symbol == Symbol.INTEGERDIVIDES){
            currentToken = lex.getToken()
            val integerDivides: Expr = IntegerDivides(inVal, exponential())
            return multiplicative2(integerDivides)
        }
        return inVal
    }

    private fun exponential(): Expr{
        val result: Expr = unary()
        return exponential2(result)
    }

    private fun exponential2(inVal: Expr): Expr{
        if(currentToken.symbol == Symbol.POW){
            currentToken = lex.getToken()
            val pow: Expr = Pow(inVal, exponential2(unary()))
            return exponential2(pow)
        }
        return inVal
    }

    private fun unary(): Expr{
        if(currentToken.symbol == Symbol.PLUS){
            currentToken = lex.getToken()
            return UnaryPlus(primary())
        }
        else if(currentToken.symbol == Symbol.MINUS){
            currentToken = lex.getToken()
            return UnaryMinus(primary())
        }
        return primary()
    }

    private fun primary(): Expr {
        if (currentToken.symbol == Symbol.VARIABLE) {
            val lexeme = currentToken.lexeme
            currentToken = lex.getToken()
            return reassign(lexeme)

        } else if (currentToken.symbol == Symbol.REAL) {
            val lexeme = currentToken.lexeme
            currentToken = lex.getToken()
            return Real(lexeme.toDouble())
        } else if (currentToken.symbol == Symbol.LPAREN) {
            currentToken = lex.getToken()
            val result: Expr = additive()
            if (currentToken.symbol == Symbol.RPAREN) {
                currentToken = lex.getToken()
                return result
            } else {
                throw Error("Invalid")
            }
        } else {
            throw Error("Invalid")
        }

    }

    private fun reassign(lexeme: String): Expr{
        if (currentToken.symbol == Symbol.ASSIGN) {
            currentToken = lex.getToken()
            val result: Expr = additive()
            return Reassign(Variable(lexeme), result)
        }
        return Variable(lexeme)
    }

    private fun print(): Print {
        if (currentToken.symbol == Symbol.LPAREN) {
            currentToken = lex.getToken()
            val result: Expr = additive()
            if (currentToken.symbol == Symbol.RPAREN) {
                currentToken = lex.getToken()
                return  Print(result)
            } else {
                throw Error("Invalid")
            }

        } else {
            throw Error("Invalid")
        }
        throw Error("Invalid")
    }

    private fun define(): Assign {
        if (currentToken.symbol == Symbol.VARIABLE) {
            val variable = currentToken.lexeme
            currentToken = lex.getToken()
            if (currentToken.symbol == Symbol.ASSIGN) {
                currentToken = lex.getToken()
                val result = additive()
                return Assign(Variable(variable), result)
            } else {
                throw Error("Invalid")
            }
        } else {
            throw Error("Invalid")
        }
    }

    private fun define2(): Assign {
        if (currentToken.symbol == Symbol.VAR) {
            currentToken = lex.getToken()
            return define()

        }
        throw Error("Invalid")

    }

    private fun forLoop(): ForLoop {
        if (currentToken.symbol == Symbol.LPAREN) {
            currentToken = lex.getToken()
            val definition: Assign = define2()
            if (currentToken.symbol == Symbol.TO) {
                currentToken = lex.getToken()
                val iteration: Expr = additive()
                if (currentToken.symbol == Symbol.RPAREN) {
                    currentToken = lex.getToken()
                    if (currentToken.symbol == Symbol.BEGIN) {
                        currentToken = lex.getToken()
                        val statements: Sent? = statementList()
                        if (currentToken.symbol == Symbol.END) {
                            currentToken = lex.getToken()
                            return ForLoop(definition, iteration, statements)
                        } else {
                            throw Error("Invalid")
                        }

                    } else {
                        throw Error("Invalid")
                    }
                } else {
                    throw Error("Invalid")
                }
            } else {
                throw Error("Invalid")
            }
        } else {
            throw Error("Invalid")
        }
    }


    fun parse(): Program{
        if(currentToken.symbol == Symbol.EOF){
            throw Error("Invalid")
        }
        return Program(expr())
    }
}

val vars = HashMap<String, Double>()
var outputStream: OutputStream  = System.out

interface  Evaluable {
    fun eval(): Double
}

interface Expr : Evaluable{
    override fun toString(): String
    fun toXML(d: OutputStream)
}


class Plus(
    val e1: Expr,
    val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1+$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<plus>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</plus>".toByteArray())
    }
    override fun eval(): Double{
        return e1.eval() + e2.eval()
    }
}
class Minus(
    val e1: Expr,
    val e2: Expr
) :Expr {
    override fun toString(): String {
        return "($e1-$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<minus>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</minus>".toByteArray())
    }
    override fun eval(): Double{
        return e1.eval() - e2.eval()
    }
}
class Times(
    val e1: Expr,
    val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1*$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<times>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</times>".toByteArray())
    }
    override fun eval(): Double{
        return e1.eval() * e2.eval()
    }
}
class Divides(
    val e1: Expr,
    val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1/$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<divides>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</divides>".toByteArray())
    }
    override fun eval(): Double{
        return e1.eval() / e2.eval()
    }
}
class IntegerDivides(
    val e1: Expr,
    val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1//$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<integer-divides>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</integer-divides>".toByteArray())
    }
    override fun eval(): Double{
        return floor(e1.eval() / e2.eval())
    }
}
class UnaryPlus(
    val e: Expr
) : Expr{
    override fun toString(): String {
        return "(+$e)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<unary-plus>".toByteArray())
        e.toXML(d)
        d.write("</unary-plus>".toByteArray())
    }
    override fun eval(): Double{
        return e.eval()
    }
}
class UnaryMinus(
    val e: Expr
) : Expr {
    override fun toString(): String {
        return "(-$e)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<unary-minus>".toByteArray())
        e.toXML(d)
        d.write("</unary-minus>".toByteArray())
    }
    override fun eval(): Double{
        return -e.eval()
    }
}
class Pow(
    val e1: Expr,
    val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1^$e2)"
    }
    override fun toXML(d: OutputStream) {
        d.write("<pow>".toByteArray())
        e1.toXML(d)
        e2.toXML(d)
        d.write("</pow>".toByteArray())
    }
    override fun eval(): Double{
        return e1.eval().pow(e2.eval())
    }
}
class Real(
    val d1: Double
) : Expr {
    override fun toString(): String {
        return d1.toString()
    }
    override fun toXML(d: OutputStream) {
        d.write("<real>$d1</real>".toByteArray())
    }
    override fun eval(): Double{
        return d1
    }

}
class Variable(
    val s: String
) : Expr {
    override fun toString(): String {
        return s
    }
    override fun toXML(d: OutputStream) {
        d.write("<variable>$s</variable>".toByteArray())
    }
    override fun eval(): Double {
        val result = vars[s] ?: throw Error("Uninitialized variable")
        return result
    }
}
interface  Sentence : Evaluable{
    override fun toString(): String
    fun toXML(d: OutputStream)

}
class Assign(
    val v: Variable,
    val e: Expr
) : Sentence {
    override fun toString(): String {
        return "var $v = ($e);"
    }

    override fun toXML(d: OutputStream) {
        d.write("<definition variable=\"$v\">".toByteArray())
        e.toXML(d)
        d.write("</definition>".toByteArray())
    }

    override fun eval(): Double {
        val result = e.eval()
        vars[v.toString()] = result
        return result
    }

}
class Reassign(
    val v: Variable,
    val e: Expr
) : Sentence, Expr {
    override fun toString(): String {
        return "$v = ($e);"
    }

    override fun toXML(d: OutputStream) {
        d.write("<assignment variable=\"$v\">".toByteArray())
        e.toXML(d)
        d.write("</assignment>".toByteArray())
    }
    override fun eval(): Double {
        if(!vars.containsKey(v.toString())){
            throw Error("Initializing undeclared variable")
        }
        vars[v.toString()] = e.eval()
        return e.eval()
    }
}
class Print(
    val e: Expr
) : Sentence {
    override fun toString(): String {
        return if (e is Variable || e is Real) "print ($e);"
        else "print $e;"
    }

    override fun toXML(d: OutputStream) {
        d.write("<print>".toByteArray())
        e.toXML(d)
        d.write("</print>".toByteArray())
    }

    override fun eval(): Double {
        val result = e.eval()
        outputStream.write("$result\n".toByteArray())
        return result
    }
}
class ForLoop(
    val a: Assign,
    val e: Expr,
    val s: Sent?
) : Sentence {
    override fun toString(): String {
        return "for (${a.toString().dropLast(1)}, $e) {$s}"
    }
    override fun toXML(d: OutputStream) {
        d.write("<for variable=\"${a.v}\">".toByteArray())
        a.e.toXML(d)
        e.toXML(d)
        s?.toXML(d)
        d.write("</for>".toByteArray())
    }
    override fun eval(): Double {
        a.eval()
        var result = 0.0
        while (vars[a.v.toString()]!! <= e.eval()) {
            result = s?.eval()!!

            vars[a.v.toString()] = vars[a.v.toString()]!! + 1
        }
        return result
    }
}
class Sent(
    val sent: Sentence,
    val sents: Sent? = null
) : Sentence {
    override fun toString(): String {
        return "$sent ${sents ?: ""}"
    }
    override fun toXML(d: OutputStream) {
        d.write("<seq>".toByteArray())
        sent.toXML(d)
        if(sents != null) sents.toXML(d)
        else d.write("<end></end>".toByteArray())
        d.write("</seq>".toByteArray())
    }
    override fun eval(): Double {
        sent.eval()
        sents?.eval()
        return 0.0
    }
}
/*
fun test(e: Sent){
    val ret: String = "$e"
    println(ret)
    System.out.write(
        if (Parser(Scanner(ForForeachFFFAutomaton, ret.byteInputStream())).parse()) {
            "accept".toByteArray()
        } else {
            "reject".toByteArray()
        }
    )
    print("\n")
}
*/
class Program(
    val s: Sent
){
    fun toXML(d: OutputStream) {
        d.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><program>".toByteArray())
        s.toXML(d)
        d.write("</program>".toByteArray())
    }
    fun eval(output: OutputStream){
        outputStream = output
        s.eval()
    }
}

fun validateXML(xml: String, xsd: String): Boolean {
    try {
        val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val validator = factory.newSchema(StreamSource(xsd)).newValidator()
        validator.validate(StreamSource(xml))
        return true
    } catch (e: Exception) {
        println("Validation error: ${e.message}")
        return false
    }
}


