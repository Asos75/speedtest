package org.example

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

    }

}
