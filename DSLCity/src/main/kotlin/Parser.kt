package org.example

class Parser(
    private val lex: Scanner,
    private var currentToken: Token = lex.getToken()
){
    private fun expr(): ConstructList?{
        return constructs()
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
    private fun primary(): Expr{
        if(currentToken.symbol == Symbol.REAL){
            val lexeme = currentToken.lexeme
            currentToken = lex.getToken()
            return Real(lexeme.toDouble())
        }
        else if(currentToken.symbol == Symbol.VARIABLE){
            val lexeme = currentToken.lexeme
            currentToken = lex.getToken()
            return Variable(lexeme)
        }

        else if(currentToken.symbol == Symbol.LPAREN){
            currentToken = lex.getToken()
            val result: Expr = additive()
            if(currentToken.symbol == Symbol.RPAREN){
                currentToken = lex.getToken()
                return result
            }
            throw Error("Invalid")
        }
        throw Error("Invalid")
    }

    private fun point(): Point {
        if (currentToken.symbol == Symbol.LPAREN) {
            currentToken = lex.getToken()
            val resultC1 = additive()
            if (currentToken.symbol == Symbol.TO) {
                currentToken = lex.getToken()
                val resultC2 = additive()
                if (currentToken.symbol == Symbol.RPAREN) {
                    currentToken = lex.getToken()
                    return Point(resultC1, resultC2)
                }
            }
        }

       throw Error("Invalid")
    }


    private fun constructs(): ConstructList?{
        if (currentToken.symbol == Symbol.EOF || currentToken.symbol == Symbol.END) {
            return null
        }
        return ConstructList(construct(), constructs())
    }

    private fun construct() : Construct{
        when(currentToken.symbol){
            Symbol.CITY -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val blocks = blocks()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return City(name, blocks)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            else -> throw Error("Invalid")
        }
        throw Error("Invalid")
    }

    private fun blocks(): BlockList? {
        if (currentToken.symbol == Symbol.EOF || currentToken.symbol == Symbol.END) {
            return null
        }
        return BlockList(blockTerminator(), blocks())
    }

    private fun blockTerminator(): Block {
        val result = block()
        if(currentToken.symbol == Symbol.TERM){
            currentToken  = lex.getToken()
            return result
        }
        throw Error("Invalid")
    }

    private fun block() : Block{
        when(currentToken.symbol){
            Symbol.ROAD -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return Road(name, commands)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            Symbol.BUILDING -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return Building(name, commands)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            Symbol.RIVER -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return River(name, commands)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            Symbol.TOWER -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return Tower(name, commands)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            Symbol.MEASUREMENT -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.STRING){
                    val name = currentToken.lexeme
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return Measurment(name, commands)
                        }
                    }
                }
                else throw Error("Invalid")
            }
            else -> throw Error("Invalid")
        }
        throw Error("Invalid")
    }



    private fun commands() : CommandList?{
        if (currentToken.symbol == Symbol.EOF || currentToken.symbol == Symbol.END) {
            return null
        }
        return CommandList(commandTerminator(), commands())
    }

    private fun commandTerminator(): Command {
        val result = command()
        if(currentToken.symbol == Symbol.TERM){
            currentToken  = lex.getToken()
            return result
        }
        throw Error("Invalid")
    }
    private fun command(): Command {
        when(currentToken.symbol){
            Symbol.BEND -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val p1 = point()
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2 = point()
                        if(currentToken.symbol == Symbol.TO){
                            currentToken = lex.getToken()
                            val a = additive()
                            if(currentToken.symbol == Symbol.RPAREN){
                                currentToken = lex.getToken()
                                return Bend(p1, p2, a)
                            }
                        }
                    }
                }
                throw Error("Invalid")
            }
            Symbol.LINE -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val p1 = point()
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2 = point()
                        if(currentToken.symbol == Symbol.RPAREN){
                            currentToken = lex.getToken()
                            return Line(p1, p2)
                        }
                    }
                }
                throw Error("Invalid")
            }
            Symbol.BOX -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val p1 = point()
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2 = point()
                        if(currentToken.symbol == Symbol.RPAREN){
                            currentToken = lex.getToken()
                            return Box(p1, p2)
                        }
                    }
                }
                throw Error("Invalid")
            }
            Symbol.CIRCLE -> {
                currentToken = lex.getToken()
                if (currentToken.symbol == Symbol.LPAREN) {
                    currentToken = lex.getToken()
                    val p = point()
                    if (currentToken.symbol == Symbol.TO) {
                        currentToken = lex.getToken()
                        val a = additive()
                        if (currentToken.symbol == Symbol.RPAREN) {
                            currentToken = lex.getToken()
                            return Circle(p, a)
                        }

                    }
                }
                throw Error("Invalid")
            }
            Symbol.MARKER -> {
                currentToken = lex.getToken()
                val p = point()
                return Marker(p)
            }

            else -> throw Error("Invalid")
        }
    }

    fun parse(): ConstructList? {
        if(currentToken.symbol == Symbol.EOF){
            throw Error("Invalid")
        }
        return expr()
    }
}