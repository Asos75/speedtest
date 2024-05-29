package org.example

import java.io.OutputStream
import javax.swing.text.DefaultStyledDocument.ElementSpec
import kotlin.Boolean
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.reflect.KClass
fun<T: Any> T.getClass(): KClass<T> {
    return javaClass.kotlin
}
val vars = HashMap<String, Any>()

interface Evaluable{
    fun eval()
}

//region AdditiveClassess
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

class CustomBoolean(
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

//endregion
interface PointType
class Point(
    val c1: Expr,
    val c2: Expr
) : PointType{

    override fun toString(): String{
        return "Point(${c1.eval()}, ${c2.eval()})"
    }
}

interface ObjList {
}
interface Command{
    override fun toString(): String
    fun toGEOJson(d: OutputStream)
    fun isContainedInCircle(pc: Point, r: Double): Boolean
    fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean
}

class CommandList(
    val comm: Command,
    val comms: CommandList? = null
): ObjList {
    override fun toString(): String {
        return "$comm ${comms ?: ""}"
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

    override fun isContainedInCircle(p: Point, r: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun isContainedInRectangle(p1: Point, p2: Point): Boolean {
        TODO("Not yet implemented")
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

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
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
        return Aux.isPointInCircle(p1.c1.eval(), p1.c2.eval(), pc.c1.eval(), pc.c2.eval(), r) &&
                Aux.isPointInCircle(p2.c1.eval(), p2.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
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
        return Aux.isPointInRectangle(p1.c1.eval(), p1.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),) &&
                Aux.isPointInRectangle(p2.c1.eval(), p2.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),)
    }

}

class Box(
    val pt1: PointType,
    val pt2: PointType,
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

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
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
        return Aux.isPointInCircle(p1.c1.eval(), p1.c2.eval(), pc.c1.eval(), pc.c2.eval(), r) &&
                Aux.isPointInCircle(p2.c1.eval(), p2.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
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
        return Aux.isPointInRectangle(p1.c1.eval(), p1.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),) &&
                Aux.isPointInRectangle(p2.c1.eval(), p2.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),)
    }

}

class Circle(
    val pt: PointType,
    val r: Expr
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

    override fun isContainedInCircle(pc: Point, rc: Double): Boolean {
        val circleX = pc.c1.eval()
        val circleY = pc.c2.eval()
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
        coordinates.forEachIndexed { index, point ->
            if(!Aux.isPointInCircle(point[0], point[1], circleX, circleY, rc)){
                return false
            }
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        val rectX1 = pr1.c1.eval()
        val rectY1 = pr1.c2.eval()
        val rectX2 = pr2.c1.eval()
        val rectY2 = pr2.c2.eval()
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
        coordinates.forEachIndexed { index, point ->
            if(!Aux.isPointInRectangle(point[0], point[1], rectX1, rectY1, rectX2, rectY2)){
                return false
            }
        }
        return true

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
        d.write("\"coordinates\": [ ${p.c1.eval()},${p.c2.eval()} ]".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        return Aux.isPointInCircle(p.c1.eval(), p.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else pt as Point
        return Aux.isPointInRectangle(p.c1.eval(), p.c2.eval(), pr1.c1.eval(), pr1.c2.eval(),  pr2.c1.eval(), pr2.c2.eval())
    }

}

interface Block{
    override fun toString(): String
    fun toGEOJson(d: OutputStream)
    fun isContainedInCircle(pc: Point, r: Double): Boolean
    fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean

}



var firstBlock = false
class BlockList(
    val block: Block,
    val blockList: BlockList?
): ObjList {
    override fun toString(): String {
        return "$block ${blockList ?: ""}"
    }
}
class Road(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
    private val properties: MutableList<Property> = mutableListOf(),
    private val commands: MutableList<Command> = mutableListOf()
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is Property){
                properties.add(command)
            } else {
                commands.add(command)
            }
            cl = cl?.comms
            command = cl?.comm
        }

    }
    override fun toGEOJson(d: OutputStream) {
        if(!out) return

        eval(comms)

        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach { it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        commands.forEach { it.toGEOJson(d) }
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }


}

class Building(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
    private val properties: MutableList<Property> = mutableListOf(),
    private val commands: MutableList<Command> = mutableListOf()
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is Property){
                properties.add(command)
            } else {
                commands.add(command)
            }
            cl = cl?.comms
            command = cl?.comm
        }

    }
    override fun toGEOJson(d: OutputStream) {
        if(!out) return

        eval(comms)

        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach { it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        commands.forEach { it.toGEOJson(d) }
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

}

class River(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
    private val properties: MutableList<Property> = mutableListOf(),
    private val commands: MutableList<Command> = mutableListOf()
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is Property){
                properties.add(command)
            } else {
                commands.add(command)
            }
            cl = cl?.comms
            command = cl?.comm
        }

    }
    override fun toGEOJson(d: OutputStream) {
        if(!out) return

        eval(comms)

        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach { it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        commands.forEach { it.toGEOJson(d) }
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }
}

class Tower(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
    private val properties: MutableList<Property> = mutableListOf(),
    private val commands: MutableList<Command> = mutableListOf()
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is Property){
                properties.add(command)
            } else {
                commands.add(command)
            }
            cl = cl?.comms
            command = cl?.comm
        }

    }
    override fun toGEOJson(d: OutputStream) {
        if(!out) return

        eval(comms)

        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach { it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        commands.forEach { it.toGEOJson(d) }
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }
}

class Measurment(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
    private val properties: MutableList<Property> = mutableListOf(),
    private val commands: MutableList<Command> = mutableListOf()
): Block{
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is Property){
                properties.add(command)
            } else {
                commands.add(command)
            }
            cl = cl?.comms
            command = cl?.comm
        }

    }
    override fun toGEOJson(d: OutputStream) {
        if(!out) return

        eval(comms)

        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach { it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        commands.forEach { it.toGEOJson(d) }
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
        }
        return true
    }

}

interface Property {
    fun toGEOJson(d: OutputStream)
}
class SetString(
    private val name: String,
    private val value: String,
) : Block, Command, Property {
    override fun toString(): String {
        return "$name { $value }"
    }

    override fun toGEOJson(d: OutputStream) {
        d.write(", $name: $value".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return true
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

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return true
    }
}


interface Construct{
    override fun toString(): String

    fun toGEOJson(d: OutputStream)
}

class City(
    private val name: String,
    private val blockList: BlockList? = null,
    private val properties: MutableList<Property> = mutableListOf(),
    private val blocks: MutableList<Block> = mutableListOf()

) : Construct {
    override fun toString(): String {
        return "$name { $blockList }"
    }

    fun eval(blockList: BlockList?){
        var bl = blockList
        var block = bl?.block
        while (block != null) {
            if(block is Evaluable){
                block.eval()
            }
            else if(block is If){
                eval(block.eval() as BlockList?)
            }
            else if(block is Property){
                properties.add(block)
            } else {
                blocks.add(block)
            }
            bl = bl?.blockList
            block = bl?.block
        }

    }

    override fun toGEOJson(d: OutputStream) {
        eval(blockList)

        firstBlock = true;
        d.write("{".toByteArray())
        d.write("\"type\": \"FeatureCollection\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        properties.forEach{ it.toGEOJson(d) }
        d.write("},".toByteArray())
        d.write("\"features\": [".toByteArray())
        blocks.forEach { it.toGEOJson(d) }
        d.write("]".toByteArray())
        d.write("}".toByteArray())
    }
}

class ConstructList(
    private val construct: Construct,
    private val constructList: ConstructList?
) : ObjList{
    override fun toString(): String {
        return "$construct ${constructList ?: ""}"
    }

    fun toGEOJson(d: OutputStream) {
        construct.toGEOJson(d)
        constructList?.toGEOJson(d)
    }


}

class Assign(
    private val v: Variable,
    private val e: Any
) : Construct, Block, Command, Evaluable {
    override fun eval() {
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
        else if(e is Block){
            vars[v.toString()] = e
        }
        else if(e is Command){
            vars[v.toString()] = e
        }
        else {
            vars[v.toString()] = e
        }
    }

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
        else if(e is Block){
            vars[v.toString()] = e
        }
        else if(e is Command){
            vars[v.toString()] = e
        }
        else {
            vars[v.toString()] = e
        }
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        throw Error("Assign can not be used with in/out")
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        throw Error("Assign can not be used with in/out")
    }
}

class Reassign(
    private val v: Variable,
    private val e: Any
) : Construct, Block, Command, Evaluable {
    override fun eval() {
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
        else if(e is Block){
            vars[v.toString()] = e
        }
        else if(e is Command){
            vars[v.toString()] = e
        }
        else {
            vars[v.toString()] = e
        }
    }

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
        else if(e is Block){
            vars[v.toString()] = e
        }
        else if(e is Command){
            vars[v.toString()] = e
        }
        else {
            vars[v.toString()] = e
        }
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        throw Error("Reassign can not be used with in/out")
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        throw Error("Reassign can not be used with in/out")
    }
}


class Variable(
    private val s: String
) : Element, PointType, Construct, Command, Block {
    override fun toString(): String {
        return s
    }

    override fun toGEOJson(d: OutputStream) {
        println("Class of vars[$s]: ${vars[s]?.javaClass?.name}")
        if(vars[s] is Block){
            println(true)
        }
        if(vars[s] is Construct)
            (vars[s] as Construct).toGEOJson(d)
        else if(vars[s] is Block)
            (vars[s] as Block).toGEOJson(d)
        else if(vars[s] is Command)
            (vars[s] as Command).toGEOJson(d)
        else throw Error("Unknown Output Type")
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return (vars[s] as Command).isContainedInCircle(pc, r)
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return (vars[s] as Command).isContainedInRectangle(pr1, pr2)
    }


    override fun eval(): Any{
        println(vars[s]?.getClass())
        if(vars[s] is Double)
            return vars[s] as Double
        if(vars[s] is Point)
            return vars[s] as Point
        if(vars[s] is CustomString)
            return vars[s] as CustomString
        if(vars[s] is Construct)
            return vars[s] as Construct
        if(vars[s] is Block)
            return vars[s] as Block
        if(vars[s] is Command)
            return vars[s] as Command
        throw Error("Unknown Type")
    }
}

class Variable2(
    private val s: String
) : Expr {
    override fun toString(): String {
        return s
    }
    override fun eval(): Double{
        return vars[s] as Double

    }
}


class CustomString(
    private val s: String
): Element{
    override fun eval(): Any {
        return s
    }

}
interface Comparator{
    fun eval(): Boolean
}
class Greater(
    private val e1: Element,
    private val e2: Element
) : Comparator{
    override fun eval(): Boolean {
        return ((e1.eval() as Double) > (e2.eval() as Double))
    }

}

class Lesser(
    private val e1: Element,
    private val e2: Element
) : Comparator{
    override fun eval(): Boolean {
        return ((e1.eval() as Double) < (e2.eval() as Double))
    }

}
class Equal(
    val e1: Element,
    val e2: Element
) : Comparator{
    override fun eval(): Boolean {
        val e1p = if(e1 is Variable) e1.eval() else e1
        val e2p = if(e2 is Variable) e2.eval() else e2

        if((e1p is Double) && (e2p is Double)){
            return(e1p) == (e2p)
        }
        if((e1p is Expr) && (e2p is Expr)){
            return(e1p.eval()) == (e2p.eval())
        }
        if((e1p is CustomString) && (e2p is CustomString)){
            return(e1p.eval()) == (e2p.eval())
        }
        throw Error("Invalid operands")
    }

}

class In(
    val e1: Element,
    val e2: Element
) : Comparator{
    override fun eval(): Boolean {
        val e1p = if(e1 is Variable) e1.eval() else e1
        val e2p = if(e2 is Variable) e2.eval() else e2

        if(e2p is Circle){
            val p : Point = if((e2p as Circle).pt is Variable) {
                ((e2p as Circle).pt as Variable).eval() as Point
            } else {
                (e2p as Circle).pt as Point
            }
            if(e1p is Block){
                if(!e1p.isContainedInCircle(p, e2p.r.eval())){
                    return false
                }
            } else throw Error("First parameter of in must be a block")
        } else if (e2p is Box){
            val p1 : Point = if((e2p as Box).pt1 is Variable) {
                ((e2p as Box).pt1 as Variable).eval() as Point
            } else {
                (e2p as Box).pt1 as Point
            }
            val p2 : Point = if((e2p as Box).pt2 is Variable) {
                ((e2p as Box).pt2 as Variable).eval() as Point
            } else {
                (e2p as Box).pt2 as Point
            }
            if(e1p is Block){
                if(!e1p.isContainedInRectangle(p1, p2)){
                    return false
                }
            } else throw Error("First parameter of in must be a block")
        } else throw Error("Second parameter in of can only be simple blocks with area (Box, Circle)")
        return true
    }
}

class Out(
    val e1: Element,
    val e2: Element
) : Comparator{
    override fun eval(): Boolean {
        val e1p = if(e1 is Variable) e1.eval() else e1
        val e2p = if(e2 is Variable) e2.eval() else e2

        if(e2p is Circle){

        } else if (e2p is Box){

        } else throw Error("Second parameter in of out can only be simple blocks with area (Box, Circle)")
        return true
    }

}

class If(
    val c: Comparator,
    val b: ObjList?
) : Block, Command, Construct {
    fun eval(): ObjList? {
        if(c.eval()){
            return b
        }
        return null
    }

    override fun toString(): String {
        return b.toString()
    }



    override fun toGEOJson(d: OutputStream) {
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        if(b is CommandList) {
            val comms = b as CommandList
            var comm = comms?.comm
            while (comm != null) {
                if (!comm.isContainedInCircle(pc, r)) {
                    return false
                }
                comm = comms?.comms?.comm
            }
            return true
        } else throw Error("Invalid")
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        if (b is CommandList) {
            val comms = b as CommandList
            var comm = comms?.comm
            while (comm != null) {
                if (!comm.isContainedInRectangle(pr1, pr2)) {
                    return false
                }
                comm = comms?.comms?.comm
            }
            return true
        } else throw Error("Invalid")
    }
}

class ForLoop(
    val a: Assign,
    val e: Expr,
    val b: ObjList?
): Construct, Block, Command{
    override fun toString(): String {
        return b.toString()
    }

    override fun toGEOJson(d: OutputStream) {
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        if(b is CommandList) {
            val comms = b as CommandList
            var comm = comms?.comm
            while (comm != null) {
                if (!comm.isContainedInCircle(pc, r)) {
                    return false
                }
                comm = comms?.comms?.comm
            }
            return true
        } else throw Error("Invalid")
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        if (b is CommandList) {
            val comms = b as CommandList
            var comm = comms?.comm
            while (comm != null) {
                if (!comm.isContainedInRectangle(pr1, pr2)) {
                    return false
                }
                comm = comms?.comms?.comm
            }
            return true
        } else throw Error("Invalid")
    }

}