package org.example

class Parser(
    private val lex: Scanner,
    private var currentToken: Token = lex.getToken()
){
    private fun expr(): ConstructList?{
        return constructs()
    }
//region Additive
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
            return Variable2(lexeme) as Expr
        }
        else if(currentToken.symbol == Symbol.TRUE){
            currentToken = lex.getToken()
            return CustomBoolean(true)
        }
        else if(currentToken.symbol == Symbol.FALSE){
            currentToken = lex.getToken()
            return CustomBoolean(false)
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
//endregion
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

    private fun set(): Property {
        if(currentToken.symbol == Symbol.LPAREN) {
            currentToken = lex.getToken()
            if (currentToken.symbol == Symbol.STRING) {
                val name = currentToken.lexeme
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TO) {
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.STRING || currentToken.symbol == Symbol.TRUE || currentToken.symbol == Symbol.FALSE){
                        val value = currentToken.lexeme
                        currentToken = lex.getToken()
                        if(currentToken.symbol == Symbol.RPAREN){
                            currentToken = lex.getToken()
                            return SetString(name, value)
                        }
                    }
                    else if(currentToken.symbol == Symbol.VARIABLE || currentToken.symbol == Symbol.REAL){
                        val value = additive()
                        if(currentToken.symbol == Symbol.RPAREN){
                            currentToken = lex.getToken()
                            return SetReal(name, value)
                        }
                    }
                }
            }
        }
        throw Error("Invalid")
    }

    private fun assignment(): Assign {
        if(currentToken.symbol == Symbol.VARIABLE){
            val variable = Variable(currentToken.lexeme)
            currentToken = lex.getToken()
            if(currentToken.symbol == Symbol.ASSIGN){
                currentToken = lex.getToken()
                return assignment2(variable)
            }
        }
        throw Error("Invalid Assignment")
    }

    private fun assignment2(variable: Variable): Assign {
        when (currentToken.symbol) {
            Symbol.POINT -> {
                currentToken = lex.getToken()
                if (currentToken.symbol == Symbol.LPAREN) {
                    currentToken = lex.getToken()
                    val resultC1 = additive()
                    if (currentToken.symbol == Symbol.TO) {
                        currentToken = lex.getToken()
                        val resultC2 = additive()
                        if (currentToken.symbol == Symbol.RPAREN) {
                            currentToken = lex.getToken()
                            return Assign(variable, Point(resultC1, resultC2))
                        }
                    }
                }
                throw Error("Invalid Assignment")
            }

            Symbol.ROAD, Symbol.BUILDING, Symbol.RIVER, Symbol.TOWER, Symbol.MEASUREMENT -> {
                return Assign(variable, block())
            }

            Symbol.BEND, Symbol.LINE, Symbol.BOX, Symbol.CIRCLE, Symbol.MARKER -> {
                return Assign(variable, command())
            }

            Symbol.STRING -> {
                val str = CustomString(currentToken.lexeme)
                currentToken = lex.getToken()
                return Assign(variable, str)
            }

            else -> {
                return Assign(variable, additive())
            }
        }
    }

    private fun reassignment(inVal: Variable): Reassign {
        if(currentToken.symbol == Symbol.ASSIGN){
            currentToken = lex.getToken()
            return reassignment2(inVal)
        }
        throw Error("Invalid ReAssignment")
    }

    fun reassignment2(inVal: Variable): Reassign{
        when(currentToken.symbol){
            Symbol.LPAREN -> {
                currentToken = lex.getToken()
                val resultC1 = additive()
                if (currentToken.symbol == Symbol.TO) {
                    currentToken = lex.getToken()
                    val resultC2 = additive()
                    if (currentToken.symbol == Symbol.RPAREN) {
                        currentToken = lex.getToken()
                        return Reassign(inVal, Point(resultC1, resultC2))
                    }
                }
                throw Error("Invalid")
            }
            Symbol.ROAD, Symbol.BUILDING, Symbol.RIVER, Symbol.TOWER, Symbol.MEASUREMENT -> {
                return Reassign(inVal, block())
            }
            Symbol.BEND, Symbol.LINE, Symbol.BOX, Symbol.CIRCLE, Symbol.MARKER ->{
                return Reassign(inVal, command())
            }
            Symbol.STRING -> {
                val str = CustomString(currentToken.lexeme)
                currentToken = lex.getToken()
                return Reassign(inVal, str)
            }
            else -> {
                return Reassign(inVal, additive())
            }
        }
    }

    private fun compare() : Comparator {
        println(currentToken.lexeme)
        val e1 = if(currentToken.symbol == Symbol.STRING){
            val str = CustomString(currentToken.lexeme)
            currentToken = lex.getToken()
            str
        } else if(currentToken.symbol == Symbol.VARIABLE){
            val variable = Variable(currentToken.lexeme)
            currentToken = lex.getToken()
            variable
        }
        else additive()
        when(currentToken.symbol) {
            Symbol.GREATER -> {
                currentToken = lex.getToken()
                val e2 = additive()
                return Greater(e1, e2)
            }
            Symbol.LESSER -> {
                currentToken = lex.getToken()
                val e2 = additive()
                return Lesser(e1, e2)
            }
            Symbol.EQUALS -> {
                currentToken = lex.getToken()
                val e2 = if(currentToken.symbol == Symbol.STRING){
                    val str = CustomString(currentToken.lexeme)
                    currentToken = lex.getToken()
                    str
                } else if(currentToken.symbol == Symbol.VARIABLE){
                    val variable = Variable(currentToken.lexeme)
                    currentToken = lex.getToken()
                    variable
                } else additive()
                return Equal(e1, e2)
            }
            Symbol.IN -> {
                currentToken = lex.getToken()
                val e2 = if(currentToken.symbol == Symbol.STRING){
                    val str = CustomString(currentToken.lexeme)
                    currentToken = lex.getToken()
                    str
                } else if(currentToken.symbol == Symbol.VARIABLE){
                    val variable = Variable(currentToken.lexeme)
                    currentToken = lex.getToken()
                    variable
                } else additive()
                return In(e1, e2)
            }
            Symbol.OUT -> {
                currentToken = lex.getToken()
                val e2 = if(currentToken.symbol == Symbol.STRING){
                    val str = CustomString(currentToken.lexeme)
                    currentToken = lex.getToken()
                    str
                } else if(currentToken.symbol == Symbol.VARIABLE){
                    val variable = Variable(currentToken.lexeme)
                    currentToken = lex.getToken()
                    variable
                } else additive()
                return Out(e1, e2)
            }
            else -> throw Error("Invalid Compare")
        }
        throw Error("Invalid Compare")
    }
//region constructs
    private fun constructs(): ConstructList?{
        if (currentToken.symbol == Symbol.EOF || currentToken.symbol == Symbol.END) {
            return null
        }
        return ConstructList(constructTerminator(), constructs())
    }

    private fun constructTerminator(): Construct{
        val result = construct()
        if(currentToken.symbol == Symbol.TERM){
            currentToken  = lex.getToken()
            return result
        }
        throw Error("Missing terminator")
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
            Symbol.LET ->{
                currentToken = lex.getToken()
                val result = assignment()
                if(currentToken.symbol == Symbol.TERM){
                    currentToken = lex.getToken()
                    return result
                }
                throw Error("Missing construct Terminator")
            }
            Symbol.VARIABLE -> {
                val variable = currentToken.lexeme
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TERM){
                    return Variable(variable)
                }
                val result = reassignment(Variable(variable))
                if(currentToken.symbol == Symbol.TERM){
                    currentToken = lex.getToken()
                    return result
                }
                throw Error("Missing construct Terminator")
            }
            Symbol.IF -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val compare = compare()
                    if(currentToken.symbol == Symbol.RPAREN){
                        currentToken = lex.getToken()
                        if(currentToken.symbol == Symbol.BEGIN){
                            currentToken = lex.getToken()
                            val constructList = constructs()
                            if(currentToken.symbol == Symbol.END){
                                currentToken = lex.getToken()
                                return If(compare, constructList)
                            }
                        }
                    }
                }
                throw Error("Invalid")
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
        throw Error("Missing terminator")
    }
//endregion
//region blocks
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
            Symbol.SET -> {
                currentToken = lex.getToken()
                return set() as Block
            }
            Symbol.LET ->{
                currentToken = lex.getToken()
                return  assignment()
            }
            Symbol.VARIABLE -> {
                val variable = currentToken.lexeme
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TERM){
                    return Variable(variable)
                }
                return reassignment(Variable(variable))
            }
            Symbol.FOREACH -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.VARIABLE){
                    val res = Variable(currentToken.lexeme)
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.BEGIN){
                        currentToken = lex.getToken()
                        val commands = commands()
                        if(currentToken.symbol == Symbol.END){
                            currentToken = lex.getToken()
                            return ForEach(res, commands)
                        }
                    }
                }
                throw Error("Invalid")
            }
            Symbol.IF -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val compare = compare()
                    if(currentToken.symbol == Symbol.RPAREN){
                        currentToken = lex.getToken()
                        if(currentToken.symbol == Symbol.BEGIN){
                            currentToken = lex.getToken()
                            val blocklist = blocks()
                            if(currentToken.symbol == Symbol.END){
                                currentToken = lex.getToken()
                                return If(compare, blocklist)
                            }
                        }
                    }
                }
            }
            Symbol.FOR ->  {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.LET) {
                        currentToken = lex.getToken()
                        val assign = assignment()
                        if (currentToken.symbol == Symbol.TO) {
                            currentToken = lex.getToken()
                            val add = additive()
                            if (currentToken.symbol == Symbol.RPAREN) {
                                currentToken = lex.getToken()
                                if (currentToken.symbol == Symbol.BEGIN) {
                                    currentToken = lex.getToken()
                                    val blocks = blocks()
                                    if (currentToken.symbol == Symbol.END) {
                                        currentToken = lex.getToken()
                                        return ForLoop(assign, add, blocks)
                                    }
                                }
                            }
                        }
                    }
                }
                throw Error("Invalid")
            }
            else -> throw Error("Invalid")
        }
        throw Error("Invalid")
    }

//endregion
//region commands

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
        throw Error("Missing terminator")
    }
    private fun command(): Command {
        when(currentToken.symbol){
            Symbol.BEND -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val p1 = if(currentToken.symbol == Symbol.VARIABLE){
                        val variable = Variable(currentToken.lexeme)
                        currentToken = lex.getToken()
                        variable
                    } else {
                        point()
                    }
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2= if(currentToken.symbol == Symbol.VARIABLE){
                            val variable = Variable(currentToken.lexeme)
                            currentToken = lex.getToken()
                            variable
                        } else {
                            point()
                        }
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
                    val p1 = if(currentToken.symbol == Symbol.VARIABLE){
                        val variable = Variable(currentToken.lexeme)
                        currentToken = lex.getToken()
                        variable
                    } else {
                        point()
                    }
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2= if(currentToken.symbol == Symbol.VARIABLE){
                            val variable = Variable(currentToken.lexeme)
                            currentToken = lex.getToken()
                            variable
                        } else {
                            point()
                        }
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
                    val p1 = if(currentToken.symbol == Symbol.VARIABLE){
                        val variable = Variable(currentToken.lexeme)
                        currentToken = lex.getToken()
                        variable
                    } else {
                        point()
                    }
                    if(currentToken.symbol == Symbol.TO){
                        currentToken = lex.getToken()
                        val p2= if(currentToken.symbol == Symbol.VARIABLE){
                            val variable = Variable(currentToken.lexeme)
                            currentToken = lex.getToken()
                            variable
                        } else {
                            point()
                        }
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
                    val p = if(currentToken.symbol == Symbol.VARIABLE){
                        val variable = Variable(currentToken.lexeme)
                        currentToken = lex.getToken()
                        variable
                    } else {
                        point()
                    }
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
                val p = if(currentToken.symbol == Symbol.VARIABLE){
                    val variable = Variable(currentToken.lexeme)
                    currentToken = lex.getToken()
                    variable
                } else {
                    point()
                }
                return Marker(p)
            }
            Symbol.SET -> {
                currentToken = lex.getToken()
                return set() as Command
            }
            Symbol.LET ->{
                currentToken = lex.getToken()
                return  assignment()
            }
            Symbol.VARIABLE -> {
                val variable = currentToken.lexeme
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TERM){
                    return Variable(variable)
                }
                return reassignment(Variable(variable))
            }
            Symbol.HIGHLIGHT -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TRUE){
                    currentToken = lex.getToken()
                    return SetString("\"highlighted\"", "true")
                }
                else if(currentToken.symbol == Symbol.FALSE){
                    currentToken = lex.getToken()
                    return SetString("\"highlighted\"", "false")
                }
                throw Error("Invalid")
            }
            Symbol.OUTPUT -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.TRUE){
                    currentToken = lex.getToken()
                    return SetString("\"output\"", "true")
                }
                else if(currentToken.symbol == Symbol.FALSE){
                    currentToken = lex.getToken()
                    return SetString("\"output\"", "false")
                }
                throw Error("Invalid")
            }
            Symbol.IF -> {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    val compare = compare()
                    if(currentToken.symbol == Symbol.RPAREN){
                        currentToken = lex.getToken()
                        if(currentToken.symbol == Symbol.BEGIN){
                            currentToken = lex.getToken()
                            val commandList = commands()
                            if(currentToken.symbol == Symbol.END){
                                currentToken = lex.getToken()
                                return If(compare, commandList)
                            }
                        }
                    }
                }
                throw Error("Invalid")
            }
            Symbol.FOR ->  {
                currentToken = lex.getToken()
                if(currentToken.symbol == Symbol.LPAREN){
                    currentToken = lex.getToken()
                    if(currentToken.symbol == Symbol.LET) {
                        currentToken = lex.getToken()
                        val assign = assignment()
                        if (currentToken.symbol == Symbol.TO) {
                            currentToken = lex.getToken()
                            val add = additive()
                            if (currentToken.symbol == Symbol.RPAREN) {
                                currentToken = lex.getToken()
                                if (currentToken.symbol == Symbol.BEGIN) {
                                    currentToken = lex.getToken()
                                    val commands = commands()
                                    if (currentToken.symbol == Symbol.END) {
                                        currentToken = lex.getToken()
                                        return ForLoop(assign, add, commands)
                                    }
                                }
                            }
                        }
                    }
                }
                throw Error("Invalid")
            }
            else -> throw Error("Invalid")
        }
    }
//endregion
    fun parse(): ConstructList? {
        if(currentToken.symbol == Symbol.EOF){
            throw Error("Invalid")
        }
        return expr()
    }
}