package org.example

import java.io.OutputStream
import kotlin.math.floor
import kotlin.math.pow

val vars = HashMap<String, Double>()
interface Expr{
    override fun toString(): String
    fun toXML(d: OutputStream)
    fun eval(): Double
}

class Plus(
    private val e1: Expr,
    private val e2: Expr
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
    private val e1: Expr,
    private val e2: Expr
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
    private val e1: Expr,
    private val e2: Expr
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
    private val e1: Expr,
    private val e2: Expr
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
    private val e1: Expr,
    private val e2: Expr
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
    private val e: Expr
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
    private val e: Expr
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
    private val e1: Expr,
    private val e2: Expr
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
    private val d1: Double
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
    private val s: String
) : Expr {
    override fun toString(): String {
        return s
    }
    override fun toXML(d: OutputStream) {
        d.write("<variable>$s</variable>".toByteArray())
    }
    override fun eval(): Double{
        return vars[s] ?: throw Error("Uninitialized variable")
    }
}
class Program(
    private val e: Expr
){
    fun toXML(d: OutputStream) {
        d.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><program>".toByteArray())
        e.toXML(d)
        d.write("</program>".toByteArray())
    }
    fun eval(): Double{
        return e.eval()
    }

}
class Point(
    private val c1: Expr,
    private val c2: Expr
){
    override fun toString(): String{
        return "Point(${c1.eval()}, ${c2.eval()})"
    }
}

interface Command{
    override fun toString(): String
}

class CommandList(
    private val comm: Command,
    private val comms: CommandList? = null
){
    override fun toString(): String {
        return "$comm ${comms ?: ""}"
    }
}

class Bend(
    private val p1: Point,
    private val p2: Point,
    private val a: Expr
): Command{
    override fun toString(): String {
        return "Bend(${p1},${p2},${a.eval()})"
    }

}

class Line(
    private val p1: Point,
    private val p2: Point,
): Command{
    override fun toString(): String {
        return "Line(${p1},${p2})"
    }

}

class Box(
    private val p1: Point,
    private val p2: Point,
): Command{
    override fun toString(): String {
        return "Box(${p1},${p2})"
    }

}

class Circle(
    private val p: Point,
    private val r: Expr
): Command{
    override fun toString(): String {
        return "Circle(${p},${r.eval()})"
    }

}

class Marker(
    private val p: Point,
): Command{
    override fun toString(): String {
        return "Marker(${p})"
    }

}

interface Block{
    override fun toString(): String
}
class BlockList(
    private val block: Block,
    private val blockList: BlockList?
) {
    override fun toString(): String {
        return "$block ${blockList ?: ""}"
    }
}
class Road(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

}

class Building(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

}

class River(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

}

class Tower(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

}

class Measurment(
    private val name: String,
    private val comms: CommandList? = null
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

}


interface Construct{
    override fun toString(): String
}

class City(
    private val name: String,
    private val blocks: BlockList? = null
) : Construct {
    override fun toString(): String {
        return "$name { $blocks }"
    }
}

class ConstructList(
    private val construct: Construct,
    private val constructList: ConstructList?
) {
    override fun toString(): String {
        return "$construct ${constructList ?: ""}"
    }
}