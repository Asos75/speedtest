package org.example

object ForForeachFFFAutomaton : DFA {
    override val states = (1..64).toSet()
    override val alphabet = 0..255
    override val startState = 1
    override val finalStates = setOf(
        2,
        4,
        5,
        6,
        7,
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
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        47,
        48,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        56,
        57
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

    private fun setTransition(from: Int, chr: Char, to: Int, redirect: Int) {
        setTransition(from, CharTypes.LETTERSUPERCASE, redirect)
        setTransition(from, CharTypes.LETTERSLOWERCASE, redirect)
        setTransition(from, CharTypes.NUMBERS, redirect)
        setTransition(from, '_', redirect)
        setTransition(from, chr, to)
    }

    fun closeLine(from: Int, redirect: Int){
        setTransition(from, CharTypes.LETTERSUPERCASE, redirect)
        setTransition(from, CharTypes.LETTERSLOWERCASE, redirect)
        setTransition(from, CharTypes.NUMBERS, redirect)
        setTransition(from, '_', redirect)
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

            CharTypes.ALL -> {
                for (i in 32..126) {
                    setTransition(from, i, to)
                }
            }
        }

    }

    init {
        setTransition(startState, EOF, 7)
//region SKIPS:
        setTransition(startState, ' ', 6)
        setTransition(startState, '\t', 6)
        setTransition(startState, '\n', 6)
        setTransition(startState, '\r', 6)
        setTransition(6, ' ', 6)
        setTransition(6, '\t', 6)
        setTransition(6, '\n', 6)
        setTransition(6, '\r', 6)
//endregion
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
        setTransition(5, CharTypes.NUMBERS, 5)
        setTransition(5, '_', 5)
//endregion
//region STRINGS
        setTransition(startState, '"', 8)
        setTransition(8, CharTypes.ALL, 9)
        setTransition(9, CharTypes.ALL, 9)
        setTransition(9, '"', 10)
//endregion
//region OPERATORS
        setTransition(startState, '+', 11)
        setTransition(startState, '-', 12)
        setTransition(startState, '*', 13)
        setTransition(startState, '/', 14)
        setTransition(14, '/', 15)
        setTransition(startState, '^', 16)
        setTransition(startState, '(', 17)
        setTransition(startState, ')', 18)
        setTransition(startState, '=', 19)
        setTransition(19, '=', 20)
        setTransition(startState, ';', 21)
        setTransition(startState, '{', 22)
        setTransition(startState, '}', 23)
        setTransition(startState, ',', 24)
        setTransition(startState, '<', 25)
        setTransition(startState, '>', 26)
        setTransition(startState, '[', 27)
        setTransition(startState, ']', 28)
        setTransition(startState, '.', 29)

//region RESERVED WORDS
    //bend
        setTransition(startState, 'b', 30)
        setTransition(30, CharTypes.LETTERSUPERCASE, 5)
        setTransition(30, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(30, CharTypes.NUMBERS, 5)
        setTransition(30, '_', 5)
        setTransition(30, 'e', 31)
        setTransition(31, CharTypes.LETTERSUPERCASE, 5)
        setTransition(31, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(31, CharTypes.NUMBERS, 5)
        setTransition(31, '_', 5)
        setTransition(31, 'n', 32)
        setTransition(32, CharTypes.LETTERSUPERCASE, 5)
        setTransition(32, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(32, CharTypes.NUMBERS, 5)
        setTransition(32, '_', 5)
        setTransition(32, 'd', 33)
        setTransition(33, CharTypes.LETTERSUPERCASE, 5)
        setTransition(33, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(33, CharTypes.NUMBERS, 5)
        setTransition(33, '_', 5)
    //box
        setTransition(30, 'o', 34)
        setTransition(34, CharTypes.LETTERSUPERCASE, 5)
        setTransition(34, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(34, CharTypes.NUMBERS, 5)
        setTransition(34, '_', 5)
        setTransition(34, 'x', 35)
        setTransition(35, CharTypes.LETTERSUPERCASE, 5)
        setTransition(35, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(35, CharTypes.NUMBERS, 5)
        setTransition(35, '_', 5)
    //building
        setTransition(30, 'u', 36)
        setTransition(36, CharTypes.LETTERSUPERCASE, 5)
        setTransition(36, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(36, CharTypes.NUMBERS, 5)
        setTransition(36, '_', 5)
        setTransition(36, 'i', 37)
        setTransition(37, CharTypes.LETTERSUPERCASE, 5)
        setTransition(37, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(37, CharTypes.NUMBERS, 5)
        setTransition(37, '_', 5)
        setTransition(37, 'l', 38)
        setTransition(38, CharTypes.LETTERSUPERCASE, 5)
        setTransition(38, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(38, CharTypes.NUMBERS, 5)
        setTransition(38, '_', 5)
        setTransition(38, 'd', 39)
        setTransition(39, CharTypes.LETTERSUPERCASE, 5)
        setTransition(39, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(39, CharTypes.NUMBERS, 5)
        setTransition(39, '_', 5)
        setTransition(39, 'i', 40)
        setTransition(40, CharTypes.LETTERSUPERCASE, 5)
        setTransition(40, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(40, CharTypes.NUMBERS, 5)
        setTransition(40, '_', 5)
        setTransition(40, 'n', 41)
        setTransition(41, CharTypes.LETTERSUPERCASE, 5)
        setTransition(41, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(41, CharTypes.NUMBERS, 5)
        setTransition(41, '_', 5)
        setTransition(41, 'g', 42)
        setTransition(42, CharTypes.LETTERSUPERCASE, 5)
        setTransition(42, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(42, CharTypes.NUMBERS, 5)
        setTransition(42, '_', 5)
    //circle
        setTransition(startState, 'c', 43)
        setTransition(43, CharTypes.LETTERSUPERCASE, 5)
        setTransition(43, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(43, CharTypes.NUMBERS, 5)
        setTransition(43, '_', 5)
        setTransition(43, 'i', 44)
        setTransition(44, CharTypes.LETTERSUPERCASE, 5)
        setTransition(44, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(44, CharTypes.NUMBERS, 5)
        setTransition(44, '_', 5)
        setTransition(44, 'r', 45)
        setTransition(45, CharTypes.LETTERSUPERCASE, 5)
        setTransition(45, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(45, CharTypes.NUMBERS, 5)
        setTransition(45, '_', 5)
        setTransition(45, 'c', 46)
        setTransition(46, CharTypes.LETTERSUPERCASE, 5)
        setTransition(46, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(46, CharTypes.NUMBERS, 5)
        setTransition(46, '_', 5)
        setTransition(46, 'l', 47)
        setTransition(47, CharTypes.LETTERSUPERCASE, 5)
        setTransition(47, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(47, CharTypes.NUMBERS, 5)
        setTransition(47, '_', 5)
        setTransition(47, 'e', 48)
        setTransition(48, CharTypes.LETTERSUPERCASE, 5)
        setTransition(48, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(48, CharTypes.NUMBERS, 5)
        setTransition(48, '_', 5)
    //city
        setTransition(44, 't', 49)
        setTransition(49, CharTypes.LETTERSUPERCASE, 5)
        setTransition(49, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(49, CharTypes.NUMBERS, 5)
        setTransition(49, '_', 5)
        setTransition(49, 'y', 50)
        setTransition(50, CharTypes.LETTERSUPERCASE, 5)
        setTransition(50, CharTypes.LETTERSLOWERCASE, 5)
        setTransition(50, CharTypes.NUMBERS, 5)
        setTransition(50, '_', 5)
    //for
        setTransition(startState, 'f', 51)
        setTransition(51, 'o', 52, 5)
        setTransition(52, 'r', 53, 5)
    //foreach
        setTransition(53, 'e', 54,5)
        setTransition(54, 'a', 55,5)
        setTransition(55, 'c', 56,5)
        setTransition(56, 'h', 57,5)
        closeLine(57,5)

//endregion
//region SYMBOLS
        setSymbol(2, Symbol.REAL)
        setSymbol(4, Symbol.REAL)
        setSymbol(5, Symbol.VARIABLE)
        setSymbol(6, Symbol.SKIP)
        setSymbol(7, Symbol.EOF)
        setSymbol(10, Symbol.STRING)
        setSymbol(11, Symbol.PLUS)
        setSymbol(12, Symbol.MINUS)
        setSymbol(13, Symbol.TIMES)
        setSymbol(14, Symbol.DIVIDES)
        setSymbol(15, Symbol.INTEGERDIVIDES)
        setSymbol(16, Symbol.POW)
        setSymbol(17, Symbol.LPAREN)
        setSymbol(18, Symbol.RPAREN)
        setSymbol(19, Symbol.ASSIGN)
        setSymbol(20, Symbol.EQUALS)
        setSymbol(21, Symbol.TERM)
        setSymbol(22, Symbol.BEGIN)
        setSymbol(23, Symbol.END)
        setSymbol(24, Symbol.TO)
        setSymbol(25, Symbol.LESSER)
        setSymbol(26, Symbol.GREATER)
        setSymbol(27, Symbol.LSQUARE)
        setSymbol(28, Symbol.RSQUARE)
        setSymbol(29, Symbol.DOT)
        setSymbol(30, Symbol.VARIABLE)
        setSymbol(31, Symbol.VARIABLE)
        setSymbol(32, Symbol.VARIABLE)
        setSymbol(33, Symbol.BEND)
        setSymbol(34, Symbol.VARIABLE)
        setSymbol(35, Symbol.BOX)
        setSymbol(36, Symbol.VARIABLE)
        setSymbol(37, Symbol.VARIABLE)
        setSymbol(38, Symbol.VARIABLE)
        setSymbol(39, Symbol.VARIABLE)
        setSymbol(40, Symbol.VARIABLE)
        setSymbol(41, Symbol.VARIABLE)
        setSymbol(42, Symbol.BUILDING)
        setSymbol(43, Symbol.VARIABLE)
        setSymbol(44, Symbol.VARIABLE)
        setSymbol(45, Symbol.VARIABLE)
        setSymbol(46, Symbol.VARIABLE)
        setSymbol(47, Symbol.VARIABLE)
        setSymbol(48, Symbol.CIRCLE)
        setSymbol(49, Symbol.VARIABLE)
        setSymbol(50, Symbol.CITY)
        setSymbol(51, Symbol.VARIABLE)
        setSymbol(52, Symbol.VARIABLE)
        setSymbol(53, Symbol.FOR)
        setSymbol(54, Symbol.VARIABLE)
        setSymbol(55, Symbol.VARIABLE)
        setSymbol(56, Symbol.VARIABLE)
        setSymbol(57, Symbol.FOREACH)
//endregion


    }

}

fun name(value: Symbol): String {
    return when (value) {
        Symbol.REAL -> "real"
        Symbol.VARIABLE -> "variable"
        Symbol.SKIP -> "skip"
        Symbol.EOF -> "EOF"
        Symbol.STRING -> "string"
        Symbol.PLUS -> "plus"
        Symbol.MINUS -> "minus"
        Symbol.TIMES -> "times"
        Symbol.DIVIDES -> "divides"
        Symbol.INTEGERDIVIDES -> "integer-divides"
        Symbol.POW -> "pow"
        Symbol.LPAREN -> "lparen"
        Symbol.RPAREN -> "rparen"
        Symbol.ASSIGN -> "assign"
        Symbol.EQUALS -> "equals"
        Symbol.TERM -> "term"
        Symbol.BEGIN -> "begin"
        Symbol.END -> "end"
        Symbol.TO -> "to"
        Symbol.LESSER -> "lesser"
        Symbol.GREATER ->  "greater"
        Symbol.LSQUARE -> "lsquare"
        Symbol.RSQUARE -> "rsquare"
        Symbol.DOT -> "dot"
        Symbol.BEND -> "bend"
        Symbol.BOX -> "box"
        Symbol.BUILDING -> "building"
        Symbol.CIRCLE -> "circle"
        Symbol.CITY -> "city"
        Symbol.FOR -> "for"
        Symbol.FOREACH -> "foreach"
        else -> throw Error("INVALID")
    }
}