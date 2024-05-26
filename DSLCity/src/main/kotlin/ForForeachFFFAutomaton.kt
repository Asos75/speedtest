package org.example

object ForForeachFFFAutomaton : DFA {
    override val states = (1..128).toSet()
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
        57,
        58,
        59,
        60,
        61,
        62,
        63,
        64,
        65,
        66,
        67,
        68,
        69,
        70,
        71,
        72,
        73,
        74,
        75,
        76,
        77,
        78,
        79,
        80,
        81,
        82,
        83,
        84,
        85,
        86,
        87,
        88,
        89,
        90,
        91,
        92,
        93,
        94,
        95,
        96,
        97,
        98,
        99,
        100,
        101,
        102,
        103,
        104,
        105,
        106,
        107,
        108,
        109,
        110,
        111,
        112,
        113,
        114,
        115,
        116,
        117,
        118,
        119,
        120,
        121,
        122,
        123,
        124,
        125
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

    private fun closeLine(from: Int, redirect: Int){
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
        setTransition(30, 'e', 31,5)
        setTransition(31, 'n', 32,5)
        setTransition(32, 'd', 33,5)
        closeLine(33,5)

    //box
        setTransition(30, 'o', 34)
        setTransition(34, 'x', 35,5)
        closeLine(35, 5)

    //building
        setTransition(30, 'u', 36)
        setTransition(36, 'i', 37,5)
        setTransition(37, 'l', 38,5)
        setTransition(38, 'd', 39,5)
        setTransition(39, 'i', 40,5)
        setTransition(40, 'n', 41,5)
        setTransition(41, 'g', 42,5)
        closeLine(42, 5)

    //circle
        setTransition(startState, 'c', 43)
        setTransition(43, 'i', 44,5)
        setTransition(44, 'r', 45,5)
        setTransition(45, 'c', 46,5)
        setTransition(46, 'l', 47,5)
        setTransition(47, 'e', 48,5)
        closeLine(48, 5)
    //city
        setTransition(44, 't', 49)
        setTransition(49, 'y', 50, 5)
        closeLine(50, 5)
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
    //false
        setTransition(51, 'a', 115)
        setTransition(115, 'l', 116, 5)
        setTransition(116, 's', 117, 5)
        setTransition(117, 'e', 118, 5)
        closeLine(118,5)
    //fetch
        setTransition(51, 'e', 122)
        setTransition(122, 't', 123, 5)
        setTransition(123, 'c', 124, 5)
        setTransition(124, 'h', 125, 5)
        closeLine(125, 5)
    //highlight
        setTransition(startState, 'h', 58)
        setTransition(58, 'i', 59,5)
        setTransition(59, 'g', 60,5)
        setTransition(60, 'h', 61,5)
        setTransition(61, 'l', 62,5)
        setTransition(62, 'i', 63,5)
        setTransition(63, 'g', 64,5)
        setTransition(64, 'h', 65,5)
        setTransition(65, 't', 66,5)
        closeLine(66,5)
    //if
        setTransition(startState, 'i', 67)
        setTransition(67, 'f', 68, 5)
        closeLine(68,5)
    //in
        setTransition(67, 'n', 69)
        closeLine(69, 5)
    //let
        setTransition(startState, 'l', 70)
        setTransition(70, 'e', 71, 5)
        setTransition(71, 't', 72, 5)
        closeLine(72, 5)
    //line
        setTransition(70, 'i', 73)
        setTransition(73, 'n', 74, 5)
        setTransition(74, 'e', 75, 5)
        closeLine(75, 5)
    //link
        setTransition(74, 'k', 76)
        closeLine(76, 5)
    //marker
        setTransition(startState, 'm', 77)
        setTransition(77, 'a', 78, 5)
        setTransition(78, 'r', 79, 5)
        setTransition(79, 'k', 80, 5)
        setTransition(80, 'e', 81, 5)
        setTransition(81, 'r', 82, 5)
        closeLine(82, 5)
    //measurement
        setTransition(77, 'e', 83)
        setTransition(83, 'a', 84, 5)
        setTransition(84, 's', 85, 5)
        setTransition(85, 'u', 86, 5)
        setTransition(86, 'r', 87, 5)
        setTransition(87, 'e', 88, 5)
        setTransition(88, 'm', 89, 5)
        setTransition(89, 'e', 90, 5)
        setTransition(90, 'n', 91, 5)
        setTransition(91, 't', 92, 5)
        closeLine(92,5)
    //out
        setTransition(startState, 'o', 93)
        setTransition(93, 'u', 94,5)
        setTransition(94, 't', 95,5)
    //output
        setTransition(95, 'p', 96,5)
        setTransition(96, 'u', 97,5)
        setTransition(97, 't', 98,5)
        closeLine(98,5)
    //road
        setTransition(startState, 'r', 99)
        setTransition(99, 'o', 100,5)
        setTransition(100, 'a', 101,5)
        setTransition(101, 'd', 102,5)
        closeLine(102,5)
    //river
        setTransition(99, 'i', 103)
        setTransition(103, 'v', 104,5)
        setTransition(104, 'e', 105,5)
        setTransition(105, 'r', 106,5)
        closeLine(106,5)
    //tower
        setTransition(startState, 't', 107)
        setTransition(107, 'o', 108,5)
        setTransition(108, 'w', 109,5)
        setTransition(109, 'e', 110,5)
        setTransition(110, 'r', 111,5)
        closeLine(111, 5)
    //true
        setTransition(107, 'r', 119)
        setTransition(119, 'u', 120, 5)
        setTransition(120, 'e', 121, 5)
        closeLine(121, 5)
    //set
        setTransition(startState, 's', 112)
        setTransition(112, 'e', 113, 5)
        setTransition(113, 't', 114, 5)
        closeLine(114, 5)
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
        setSymbol(58, Symbol.VARIABLE)
        setSymbol(59, Symbol.VARIABLE)
        setSymbol(60, Symbol.VARIABLE)
        setSymbol(61, Symbol.VARIABLE)
        setSymbol(62, Symbol.VARIABLE)
        setSymbol(63, Symbol.VARIABLE)
        setSymbol(64, Symbol.VARIABLE)
        setSymbol(65, Symbol.VARIABLE)
        setSymbol(66, Symbol.HIGHLIGHT)
        setSymbol(67, Symbol.VARIABLE)
        setSymbol(68, Symbol.IF)
        setSymbol(69, Symbol.IN)
        setSymbol(70, Symbol.VARIABLE)
        setSymbol(71, Symbol.VARIABLE)
        setSymbol(72, Symbol.LET)
        setSymbol(73, Symbol.VARIABLE)
        setSymbol(74, Symbol.VARIABLE)
        setSymbol(75, Symbol.LINE)
        setSymbol(76, Symbol.LINK)
        setSymbol(77, Symbol.VARIABLE)
        setSymbol(78, Symbol.VARIABLE)
        setSymbol(79, Symbol.VARIABLE)
        setSymbol(80, Symbol.VARIABLE)
        setSymbol(81, Symbol.VARIABLE)
        setSymbol(82, Symbol.MARKER)
        setSymbol(83, Symbol.VARIABLE)
        setSymbol(84, Symbol.VARIABLE)
        setSymbol(85, Symbol.VARIABLE)
        setSymbol(86, Symbol.VARIABLE)
        setSymbol(87, Symbol.VARIABLE)
        setSymbol(88, Symbol.VARIABLE)
        setSymbol(89, Symbol.VARIABLE)
        setSymbol(90, Symbol.VARIABLE)
        setSymbol(91, Symbol.VARIABLE)
        setSymbol(92, Symbol.MEASUREMENT)
        setSymbol(93, Symbol.VARIABLE)
        setSymbol(94, Symbol.VARIABLE)
        setSymbol(95, Symbol.OUT)
        setSymbol(96, Symbol.VARIABLE)
        setSymbol(97, Symbol.VARIABLE)
        setSymbol(98, Symbol.OUTPUT)
        setSymbol(99, Symbol.VARIABLE)
        setSymbol(100, Symbol.VARIABLE)
        setSymbol(101, Symbol.VARIABLE)
        setSymbol(102, Symbol.ROAD)
        setSymbol(103, Symbol.VARIABLE)
        setSymbol(104, Symbol.VARIABLE)
        setSymbol(105, Symbol.VARIABLE)
        setSymbol(106, Symbol.RIVER)
        setSymbol(107, Symbol.VARIABLE)
        setSymbol(108, Symbol.VARIABLE)
        setSymbol(109, Symbol.VARIABLE)
        setSymbol(110, Symbol.VARIABLE)
        setSymbol(111, Symbol.TOWER)
        setSymbol(112, Symbol.VARIABLE)
        setSymbol(113, Symbol.VARIABLE)
        setSymbol(114, Symbol.SET)
        setSymbol(115, Symbol.VARIABLE)
        setSymbol(116, Symbol.VARIABLE)
        setSymbol(117, Symbol.VARIABLE)
        setSymbol(118, Symbol.FALSE)
        setSymbol(119, Symbol.VARIABLE)
        setSymbol(120, Symbol.VARIABLE)
        setSymbol(121, Symbol.TRUE)
        setSymbol(122, Symbol.VARIABLE)
        setSymbol(123, Symbol.VARIABLE)
        setSymbol(124, Symbol.VARIABLE)
        setSymbol(125, Symbol.FETCH)
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
        Symbol.HIGHLIGHT -> "highlight"
        Symbol.IF -> "if"
        Symbol.IN -> "in"
        Symbol.LET -> "let"
        Symbol.LINE -> "line"
        Symbol.LINK -> "link"
        Symbol.MARKER -> "marker"
        Symbol.MEASUREMENT -> "measurement"
        Symbol.OUT -> "out"
        Symbol.OUTPUT -> "output"
        Symbol.ROAD -> "road"
        Symbol.RIVER -> "river"
        Symbol.TOWER -> "tower"
        Symbol.FALSE -> "false"
        Symbol.TRUE -> "true"
        Symbol.FETCH -> "fethc"
        else -> throw Error("INVALID")
    }
}