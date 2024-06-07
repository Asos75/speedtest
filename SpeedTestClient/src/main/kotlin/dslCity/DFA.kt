package dslCity

interface DFA {
    val states: Set<Int>
    val alphabet: IntRange
    fun next(state: Int, code: Int): Int
    fun symbol(state: Int): Symbol
    val startState: Int
    val finalStates: Set<Int>
}