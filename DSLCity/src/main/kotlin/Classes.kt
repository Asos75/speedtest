package org.example

import java.io.OutputStream
import kotlin.Boolean
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin

val vars = HashMap<String, Any>()

interface Element {
    fun eval(): Any
}
interface Expr : Element{
    override fun toString(): String
    override fun eval(): Double
}

class Plus(
    private val e1: Expr,
    private val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1+$e2)"
    }
    override fun eval(): Double{
        return e1.eval() + e2.eval()
    }
}
class Minus(
    private val e1: Expr,
    private val e2: Expr
) :Expr {
    override fun toString(): String {
        return "($e1-$e2)"
    }
    override fun eval(): Double{
        return e1.eval() - e2.eval()
    }
}
class Times(
    private val e1: Expr,
    private val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1*$e2)"
    }
    override fun eval(): Double{
        return e1.eval() * e2.eval()
    }
}
class Divides(
    private val e1: Expr,
    private val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1/$e2)"
    }
    override fun eval(): Double{
        return e1.eval() / e2.eval()
    }
}
class IntegerDivides(
    private val e1: Expr,
    private val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1//$e2)"
    }
    override fun eval(): Double{
        return floor(e1.eval() / e2.eval())
    }
}
class UnaryPlus(
    private val e: Expr
) : Expr{
    override fun toString(): String {
        return "(+$e)"
    }
    override fun eval(): Double{
        return e.eval()
    }
}
class UnaryMinus(
    private val e: Expr
) : Expr {
    override fun toString(): String {
        return "(-$e)"
    }
    override fun eval(): Double{
        return -e.eval()
    }
}
class Pow(
    private val e1: Expr,
    private val e2: Expr
) : Expr {
    override fun toString(): String {
        return "($e1^$e2)"
    }
    override fun eval(): Double{
        return e1.eval().pow(e2.eval())
    }
}
class Real(
    private val d1: Double
) : Expr {
    override fun toString(): String {
        return d1.toString()
    }
    override fun eval(): Double{
        return d1
    }

}

class Boolean(
    private val s: Boolean
) : Expr {
    override fun toString(): String {
        return s.toString()
    }
    override fun eval(): Double{
        return if(s) 1.0 else 0.0
    }
}


class Program(
    private val e: Expr
){
    fun eval(): Double{
        return e.eval()
    }

}


interface PointType
class Point(
    val c1: Expr,
    val c2: Expr
) : PointType{
    override fun toString(): String{
        return "Point(${c1.eval()}, ${c2.eval()})"
    }
}

interface Command{
    override fun toString(): String
    fun toGEOJson(d: OutputStream)
}

class CommandList(
    private val comm: Command,
    private val comms: CommandList? = null
){
    override fun toString(): String {
        return "$comm ${comms ?: ""}"
    }
    fun propertiesToGEOJson(d: OutputStream) {
        if(comm is Property) {
            comm.toGEOJson(d)
        }
        comms?.propertiesToGEOJson(d)
    }
    fun commandsToGeoJson(d: OutputStream) {
        if(comm !is Property) {
            comm.toGEOJson(d)
        }

        comms?.commandsToGeoJson(d)


    }
}

class Bend(
    private val pt1: PointType,
    private val pt2: PointType,
    private val angle: Expr
): Command{
    override fun toString(): String {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        return "Bend(${p1},${p2},${angle.eval()})"
    }


    override fun toGEOJson(d: OutputStream) {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        val numSegments = 128
        val an = angle.eval()
        val p1c1 = p1.c1.eval()
        val p1c2 = p1.c2.eval()
        val p2c1 = p2.c1.eval()
        val p2c2 = p2.c2.eval()


        d.write("\"type\": \"LineString\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        

        d.write("]".toByteArray())
    }
}

class Line(
    private val pt1: PointType,
    private val pt2: PointType,
): Command{
    override fun toString(): String {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        return "Line(${p1},${p2})"
    }

    override fun toGEOJson(d: OutputStream) {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        d.write("\"type\": \"LineString\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())

        d.write("[${p1.c1.eval()}, ${p1.c2.eval()}],".toByteArray())
        d.write("[${p2.c1.eval()}, ${p2.c2.eval()}]".toByteArray())

        d.write("]".toByteArray())
    }

}

class Box(
    private val pt1: PointType,
    private val pt2: PointType,
): Command{
    override fun toString(): String {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        return "Box(${p1},${p2})"
    }

    override fun toGEOJson(d: OutputStream) {
        val p1 : Point = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2 : Point = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        val coordinates = listOf(
            listOf(p1.c1, p1.c2),
            listOf(p2.c1, p1.c2),
            listOf(p2.c1, p2.c2),
            listOf(p1.c1, p2.c2),
            listOf(p1.c1, p1.c2) // Closing the polygon
        )
        d.write("\"type\": \"Polygon\",".toByteArray())
        d.write("\"coordinates\": [ [".toByteArray())

        coordinates.forEachIndexed { index, point ->
            d.write("[${point[0]}, ${point[1]}]".toByteArray())
            if (index < coordinates.size - 1) {
                d.write(",".toByteArray())
            }
        }

        d.write("] ]".toByteArray())
    }

}

class Circle(
    private val pt: PointType,
    private val r: Expr
): Command{
    override fun toString(): String {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        return "Circle(${p},${r.eval()})"
    }

    override fun toGEOJson(d: OutputStream) {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        val numPoints = 64
        val radius = r.eval()
        val centerX = p.c1.eval()
        val centerY = p.c2.eval()

        val coordinates = (0 until numPoints).map { i ->
            val angle = 2 * Math.PI * i / numPoints
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            listOf(x, y)
        } + listOf(listOf(centerX + radius, centerY))
        d.write("\"type\": \"Polygon\",".toByteArray())
        d.write("\"coordinates\": [ [".toByteArray())

        coordinates.forEachIndexed { index, point ->
            d.write("[${point[0]}, ${point[1]}]".toByteArray())
            if (index < coordinates.size - 1) {
                d.write(",".toByteArray())
            }
        }

        d.write("] ]".toByteArray())
    }

}

class Marker(
    private var pt: PointType,
): Command{
    override fun toString(): String {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        return "Marker(${p})"
    }

    override fun toGEOJson(d: OutputStream) {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        d.write("\"type\": \"Point\",".toByteArray())
        d.write("\"coordinates\": [ ${p.c1},${p.c2} ]".toByteArray())
    }

}

interface Block{
    override fun toString(): String
    fun toGEOJson(d: OutputStream)
}
var firstBlock = false
class BlockList(
    private val block: Block,
    private val blockList: BlockList?
) {
    override fun toString(): String {
        return "$block ${blockList ?: ""}"
    }

    fun propertiesToGEOJson(d: OutputStream) {
        if(block is Property) {
            block.toGEOJson(d)
        }
        blockList?.propertiesToGEOJson(d)
    }
    fun blocksToGEOJSON(d: OutputStream) {
        if(block !is Property) {
            block.toGEOJson(d)
        }

        blockList?.blocksToGEOJSON(d)


    }
}
class Road(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        comms?.commandsToGeoJson(d)
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


}

class Building(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        comms?.propertiesToGEOJson(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        comms?.commandsToGeoJson(d)
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


}

class River(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        comms?.propertiesToGEOJson(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        comms?.commandsToGeoJson(d)
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


}

class Tower(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        comms?.propertiesToGEOJson(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        comms?.commandsToGeoJson(d)
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


}

class Measurment(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        comms?.propertiesToGEOJson(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        comms?.commandsToGeoJson(d)
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }



}

interface Property {

}
class SetString(
    private val name: String,
    private val value: String
) : Block, Command, Property {
    override fun toString(): String {
        return "$name { $value }"
    }

    override fun toGEOJson(d: OutputStream) {
        d.write(", $name: $value".toByteArray())
    }


}

class SetReal(
    private val name: String,
    private val value: Expr
) : Block, Command, Property{
    override fun toString(): String {
        return "$name { ${value.eval()} }"
    }

    override fun toGEOJson(d: OutputStream) {
        d.write(", $name: ${value.eval()}".toByteArray())
    }
}


interface Construct{
    override fun toString(): String

    fun toGEOJson(d: OutputStream)
}

class City(
    private val name: String,
    private val blocks: BlockList? = null
) : Construct {
    override fun toString(): String {
        return "$name { $blocks }"
    }

    override fun toGEOJson(d: OutputStream) {
        firstBlock = true;
        d.write("{".toByteArray())
        d.write("\"type\": \"FeatureCollection\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        blocks?.propertiesToGEOJson(d)
        d.write("},".toByteArray())
        d.write("\"features\": [".toByteArray())
        blocks?.blocksToGEOJSON(d)
        d.write("]".toByteArray())
        d.write("}".toByteArray())
    }
}

class ConstructList(
    private val construct: Construct,
    private val constructList: ConstructList?
) : Construct{
    override fun toString(): String {
        return "$construct ${constructList ?: ""}"
    }

    override fun toGEOJson(d: OutputStream) {
        construct.toGEOJson(d)
        constructList?.toGEOJson(d)
    }


}

class Assign(
    private val v: Variable,
    private val e: Any
) : Construct, Block, Command {
    override fun toString(): String {
        return "var $v = ($e);"
    }

    override fun toGEOJson(d: OutputStream) {
        if(vars.containsKey(v.toString())){
            throw Error("Variable already defined")
        }
        if(e is Expr) {
            val result = e.eval()
            vars[v.toString()] = result
        }
        else if(e is Point){
            vars[v.toString()] = e
        }
    }
}

class Reassign(
    private val v: Variable,
    private val e: Any
) : Construct, Block, Command {
    override fun toString(): String {
        return "var $v = ($e);"
    }

    override fun toGEOJson(d: OutputStream) {
        if(!vars.containsKey(v.toString())){
            throw Error("Variable not defined")
        }
        if(e is Expr){
            val result = e.eval()
            vars[v.toString()] = result
        }
        else if(e is Point){
            vars[v.toString()] = e
        }

    }
}

class Variable(
    private val s: String
) : Element, PointType {
    override fun toString(): String {
        return s
    }
    override fun eval(): Any{
        if(vars[s] is Double)
            return vars[s] as Double
        if(vars[s] is Point)
            return vars[s] as Point
        throw Error("Unknown Type")
    }
}