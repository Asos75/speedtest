package dslCity

import java.io.OutputStream
import java.util.*
import kotlin.Boolean
import kotlin.collections.HashMap
import kotlin.math.*
import kotlin.reflect.KClass
fun<T: Any> T.getClass(): KClass<T> {
    return javaClass.kotlin
}
val vars = HashMap<String, Any>()


interface Evaluable{
    fun eval()
}
interface Copyable{
    fun deepCopy(): Copyable

}
interface Saveable{
    fun deepCopy(): Saveable
    fun save()
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

interface ObjList
interface SuperType
interface Command : SuperType{
    override fun toString(): String
    fun toGEOJson(d: OutputStream)
    fun isContainedInCircle(pc: Point, r: Double): Boolean
    fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean
}

class CommandList(
    val comm: Command,
    var comms: CommandList? = null
): ObjList {
    override fun toString(): String {
        return "$comm ${comms ?: ""}"
    }

    fun deepCopy(): CommandList {
        val newComms = comms?.deepCopy()
        if(comm is Saveable) {
            val newComm = (comm as Saveable).deepCopy() as Command
            (newComm as Saveable).save()
            return CommandList(newComm, newComms)
        }
        return CommandList(comm, newComms)

    }

}

class Bend(
    private var pt1: PointType,
    private var pt2: PointType,
    private var angle: Expr
): Command, Saveable{
    override fun save() {
        pt1 = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        pt1 = Point(Real((pt1 as Point).c1.eval()), Real((pt1 as Point).c2.eval()))
        pt2 = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        pt2 = Point(Real((pt2 as Point).c1.eval()), Real((pt2 as Point).c2.eval()))
        angle = Real(angle.eval())
    }

    override fun deepCopy(): Saveable {
        val p1Copy = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2Copy = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        val newPoint1 = Point(Real(p1Copy.c1.eval()), Real(p1Copy.c2.eval()))
        val newPoint2 = Point(Real(p2Copy.c1.eval()), Real(p2Copy.c2.eval()))
        val newAngle = Real(angle.eval())
        return Bend(newPoint1, newPoint2, newAngle)
    }

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


        val points = Bezier.bend(Coordinates(p1c1, p1c2), Coordinates(p2c1, p2c2), an).toPoints(numSegments)
        var first = true
        points.forEach{
            if(!first){
                d.write(",".toByteArray())
            } else first = false
            d.write("[${it.x}, ${it.y}]".toByteArray())
        }
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
        val numSegments = 128
        val an = angle.eval()
        val p1c1 = p1.c1.eval()
        val p1c2 = p1.c2.eval()
        val p2c1 = p2.c1.eval()
        val p2c2 = p2.c2.eval()


        val points = Bezier.bend(Coordinates(p1c1, p1c2), Coordinates(p2c1, p2c2), an).toPoints(numSegments)

        points.forEach{
            if(Auiks.isPointInCircle(it.x, it.y, pc.c1.eval(), pc.c2.eval(), r)){
                return true
            }
        }

        return false
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
        val numSegments = 128
        val an = angle.eval()
        val p1c1 = p1.c1.eval()
        val p1c2 = p1.c2.eval()
        val p2c1 = p2.c1.eval()
        val p2c2 = p2.c2.eval()


        val points = Bezier.bend(Coordinates(p1c1, p1c2), Coordinates(p2c1, p2c2), an).toPoints(numSegments)

        points.forEach {
            if(Auiks.isPointInRectangle(it.x, it.y, pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval())){
                return true
            }
        }

        return true
    }
}

class Line(
    private var pt1: PointType,
    private var pt2: PointType,
): Command, Saveable{
    override fun save() {
        pt1 = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        pt2 = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
    }

    override fun deepCopy(): Saveable {
        val p1Copy = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2Copy = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        val newPoint1 = Point(Real(p1Copy.c1.eval()), Real(p1Copy.c2.eval()))
        val newPoint2 = Point(Real(p2Copy.c1.eval()), Real(p2Copy.c2.eval()))
        return Line(newPoint1, newPoint2)
    }
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

        d.write("[${p1.c1.eval()}, ${p1.c2.eval()}],".toByteArray())
        d.write("[${p2.c1.eval()}, ${p2.c2.eval()}]".toByteArray())

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
        return Auiks.isPointInCircle(p1.c1.eval(), p1.c2.eval(), pc.c1.eval(), pc.c2.eval(), r) &&
                Auiks.isPointInCircle(p2.c1.eval(), p2.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
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
        return Auiks.isPointInRectangle(p1.c1.eval(), p1.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),) &&
                Auiks.isPointInRectangle(p2.c1.eval(), p2.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),)
    }

}

class Box(
    var pt1: PointType,
    var pt2: PointType,
): Command, Saveable{
    override fun save() {
        pt1 = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        pt2 = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
    }

    override fun deepCopy() : Saveable{
        val p1Copy = if(pt1 is Variable) {
            (pt1 as Variable).eval() as Point
        } else {
            pt1 as Point
        }
        val p2Copy = if(pt2 is Variable) {
            (pt2 as Variable).eval() as Point
        } else {
            pt2 as Point
        }
        val newPoint1 = Point(Real(p1Copy.c1.eval()), Real(p1Copy.c2.eval()))
        val newPoint2 = Point(Real(p2Copy.c1.eval()), Real(p2Copy.c2.eval()))
        return Box(newPoint1, newPoint2)
    }

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
            listOf(p1.c1.eval(), p1.c2.eval()),
            listOf(p2.c1.eval(), p1.c2.eval()),
            listOf(p2.c1.eval(), p2.c2.eval()),
            listOf(p1.c1.eval(), p2.c2.eval()),
            listOf(p1.c1.eval(), p1.c2.eval()) // Closing the polygon
        )

        coordinates.forEachIndexed { index, point ->
            d.write("[${point[0]}, ${point[1]}]".toByteArray())
            if (index < coordinates.size - 1) {
                d.write(",".toByteArray())
            }
        }

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
        return Auiks.isPointInCircle(p1.c1.eval(), p1.c2.eval(), pc.c1.eval(), pc.c2.eval(), r) &&
                Auiks.isPointInCircle(p2.c1.eval(), p2.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
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
        return Auiks.isPointInRectangle(p1.c1.eval(), p1.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),) &&
                Auiks.isPointInRectangle(p2.c1.eval(), p2.c2.eval(), pr1.c1.eval(), pr1.c2.eval(), pr2.c1.eval(), pr1.c2.eval(),)
    }

}

class Circle(
    var pt: PointType,
    var r: Expr
): Command, Saveable{
    override fun save() {
        pt = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        r = Real(r.eval())
    }

    override fun deepCopy() : Saveable{
        val ptCopy = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        val newR = Real(r.eval())
        val newPoint = Point(Real(ptCopy.c1.eval()), Real(ptCopy.c2.eval()))
        return Circle(newPoint, newR)
    }
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


        val c = radius / 6371

        val lat = Math.toRadians(centerY)
        val lon = Math.toRadians(centerX)
        var firstLon: Double? = null
        var firstLat: Double? = null

        val coordinates = (0 until 360 step 10).map { i ->
            val beta = Math.toRadians(i.toDouble())
            val lat_ = asin(sin(lat) * cos(c) + cos(lat) * sin(c) * cos(beta))
            val lon_ = lon + atan2(sin(beta) * sin(c) * cos(lat), cos(c) - sin(lat) * sin(lat_))
            if(firstLat == null) firstLat = lat_
            if(firstLon == null) firstLon = lon_
            listOf(Math.toDegrees(lon_), Math.toDegrees(lat_))
        }

        coordinates.forEachIndexed { index, point ->
            d.write("[${point[0]}, ${point[1]}]".toByteArray())
            if (index < coordinates.size - 1) {
                d.write(",".toByteArray())
            }
        }
        d.write(",".toByteArray())
        d.write("[${firstLon?.let { Math.toDegrees(it) }}, ${firstLat?.let { Math.toDegrees(it) }}]".toByteArray())

    }

    override fun isContainedInCircle(pc: Point, rc: Double): Boolean {
        val circleX = pc.c1.eval()
        val circleY = pc.c2.eval()
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        val radius = r.eval()
        val centerX = p.c1.eval()
        val centerY = p.c2.eval()
        val c = radius / 6371

        val lat = Math.toRadians(centerY)
        val lon = Math.toRadians(centerX)

        val coordinates = (0 until 360 step 10).map { i ->
            val beta = Math.toRadians(i.toDouble())
            val lat_ = asin(sin(lat) * cos(c) + cos(lat) * sin(c) * cos(beta))
            val lon_ = lon + atan2(sin(beta) * sin(c) * cos(lat), cos(c) - sin(lat) * sin(lat_))

            listOf(Math.toDegrees(lon_), Math.toDegrees(lat_))
        }
        coordinates.forEachIndexed { index, point ->
            if(!Auiks.isPointInCircle(point[0], point[1], circleX, circleY, rc)){
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
        val radius = r.eval()
        val centerX = p.c1.eval()
        val centerY = p.c2.eval()
        val c = radius / 6371

        val lat = Math.toRadians(centerY)
        val lon = Math.toRadians(centerX)

        val coordinates = (0 until 360 step 10).map { i ->
            val beta = Math.toRadians(i.toDouble())
            val lat_ = asin(sin(lat) * cos(c) + cos(lat) * sin(c) * cos(beta))
            val lon_ = lon + atan2(sin(beta) * sin(c) * cos(lat), cos(c) - sin(lat) * sin(lat_))

            listOf(Math.toDegrees(lon_), Math.toDegrees(lat_))
        }
        coordinates.forEachIndexed { index, point ->
            if(!Auiks.isPointInRectangle(point[0], point[1], rectX1, rectY1, rectX2, rectY2)){
                return false
            }
        }
        return true

    }

}

class Marker(
    private var pt: PointType,
): Command, Saveable{
    override fun deepCopy(): Saveable {
        val p: Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        val newPoint = Point(Real(p.c1.eval()), Real(p.c2.eval()))
        return Marker(newPoint)
    }
    override fun save() {
        pt = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        val p1 = (pt as Point).c1.eval()
        val p2 = (pt as Point).c2.eval()
        pt =  Point(Real(p1), Real(p2))
    }

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
        d.write("[ ${p.c1.eval()},${p.c2.eval()} ]".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else {
            pt as Point
        }
        return Auiks.isPointInCircle(p.c1.eval(), p.c2.eval(), pc.c1.eval(), pc.c2.eval(), r)
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        val p : Point = if(pt is Variable) {
            (pt as Variable).eval() as Point
        } else pt as Point
        return Auiks.isPointInRectangle(p.c1.eval(), p.c2.eval(), pr1.c1.eval(), pr1.c2.eval(),  pr2.c1.eval(), pr2.c2.eval())
    }

}

abstract class Block(
    val properties: MutableMap<String, Any> = mutableMapOf(),
    val commands: MutableList<Command> = mutableListOf()
) : SuperType{
    abstract override fun toString(): String
    fun printProperties(d: OutputStream){
        properties.forEach{
            d.write(",${it.key}: ".toByteArray())
            if(it.value is Expr){
                d.write("${(it.value as Expr).eval()} ".toByteArray())
            }
            else if(it.value is Double){
                d.write("${(it.value as Double)} ".toByteArray())
            }
            else if(it.value is String){
                d.write("${it.value} ".toByteArray())
            }
            else throw Error("Invalid propery type")
        }
    }
    abstract fun toGEOJson(d: OutputStream)
    abstract fun isContainedInCircle(pc: Point, r: Double): Boolean
    abstract fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean

    abstract fun evaluate()

}

var propertyStack = Stack<MutableMap<String, Any>>()

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
): Block(), Copyable{
    override fun deepCopy(): Copyable {
        val newComms = comms?.deepCopy()
        return Road(name, newComms)
    }
    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }

    override fun evaluate(){
        eval(comms)
    }
    override fun toGEOJson(d: OutputStream) {

        if(properties["\"output\""] == "false") return


        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        var firstCommand = true
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        if(properties["\"highlighted\""] == "true") {
            d.write(",\"stroke\":\"#FF0000\"".toByteArray())
        }
        super.printProperties(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        d.write("\"type\": \"MultiLineString\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        commands.forEach {
            if(firstCommand) firstCommand = false else d.write(",".toByteArray())

            d.write("[".toByteArray())
            it.toGEOJson(d)
            d.write("]".toByteArray())
        }
        d.write("]".toByteArray())
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }


}

class Building(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
): Block(), Copyable{
    override fun deepCopy(): Copyable {
        val newComms = comms?.deepCopy()
        return Building(name, newComms)
    }


    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }

    override fun evaluate(){
        eval(comms)
    }
    override fun toGEOJson(d: OutputStream) {

        if(properties["\"output\""] == "false") return


        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        var firstCommand = true
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        if(properties["\"highlighted\""] == "true") {
            d.write(",\"stroke\":\"#FF0000\"".toByteArray())
            d.write(",\"fill\": \"#FF0000\"".toByteArray())
        }
        super.printProperties(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        d.write("\"type\": \"MultiPolygon\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        d.write("[ [".toByteArray())

        commands.forEach {
            if(firstCommand) firstCommand = false else d.write(",".toByteArray())
            it.toGEOJson(d) }
        d.write("] ]".toByteArray())

        d.write("]".toByteArray())
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

}

class River(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
): Block(), Copyable{

    override fun deepCopy(): Copyable {
        val newComms = comms?.deepCopy()
        return River(name, newComms)
    }

    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }
    override fun evaluate(){
        eval(comms)
    }
    override fun toGEOJson(d: OutputStream) {

        if(properties["\"output\""] == "false") return


        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        var firstCommand = true
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        if(properties["\"highlighted\""] == "true") {
            d.write(",\"stroke\":\"#FF0000\"".toByteArray())
        }
        super.printProperties(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        d.write("\"type\": \"MultiLineString\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        commands.forEach {
            if(firstCommand) firstCommand = false else d.write(",".toByteArray())
            d.write("[".toByteArray())
            it.toGEOJson(d)
            d.write("]".toByteArray())
        }
        d.write("]".toByteArray())
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }


    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }
}

class Tower(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,
): Block(), Copyable{
    override fun deepCopy(): Copyable {
        val newComms = comms?.deepCopy()
        return Tower(name, newComms)
    }

    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }
    override fun evaluate(){
        eval(comms)
    }
    override fun toGEOJson(d: OutputStream) {

        if(properties["\"output\""] == "false") return


        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        var firstCommand = true
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        if(properties["\"highlighted\""] == "true") {
            d.write(",\"marker-color\":\"#FF0000\"".toByteArray())
        }
        super.printProperties(d)
        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        d.write("\"type\": \"MultiPoint\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        commands.forEach {
            if(firstCommand) firstCommand = false else d.write(",".toByteArray())
            it.toGEOJson(d) }
        d.write("]".toByteArray())
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }
}

class Measurment(
    private val name: String,
    private val comms: CommandList? = null,
    val out: Boolean = true,

    ): Block(), Copyable{
    override fun deepCopy(): Copyable {
        val newComms = comms?.deepCopy()
        return Measurment(name, newComms)
    }

    override fun toString(): String {
        return "$name { $comms }"
    }

    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                eval(command.eval() as CommandList?)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }
    override fun evaluate(){
        eval(comms)
    }
    override fun toGEOJson(d: OutputStream) {

        if(properties["\"output\""] == "false") return


        if(!firstBlock){
            d.write(",".toByteArray())
        } else firstBlock = false

        var firstCommand = true
        d.write("{".toByteArray())
        d.write("\"type\": \"Feature\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        if(properties["\"highlighted\""] == "true") {
            d.write(",\"marker-color\":\"#FF0000\"".toByteArray())
        }
        super.printProperties(d)

        d.write("},".toByteArray())
        d.write("\"geometry\": {".toByteArray())
        d.write("\"type\": \"MultiPoint\",".toByteArray())
        d.write("\"coordinates\": [".toByteArray())
        commands.forEach {
            if(firstCommand) firstCommand = false else d.write(",".toByteArray())
            it.toGEOJson(d) }
        d.write("]".toByteArray())
        d.write("}".toByteArray())
        d.write("}".toByteArray())
    }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInCircle(pc, r)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        var comms = comms
        var comm = comms?.comm
        while(comm != null){
            if(!comm.isContainedInRectangle(pr1, pr2)){
                return false
            }
            comm = comms?.comms?.comm
            comms = comms?.comms
        }
        return true
    }

}

interface Property : Saveable {
    fun toGEOJson(d: OutputStream)
    fun getKey() : String
    fun add(m: MutableMap<String, Any>)
}
class SetString(
    private val name: String,
    private val value: String,
) : Block(), Command, Property {
    override fun toString(): String {
        return "$name { $value }"
    }

    override fun toGEOJson(d: OutputStream) {
        d.write(", $name: $value".toByteArray())
    }

    override fun getKey(): String = name
    override fun add(m: MutableMap<String, Any>) {
        m[name] = value
    }

    override fun deepCopy(): Saveable {
        return SetString(name, value)
    }

    override fun save() { return }


    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return true
    }

    override fun evaluate() {
        return
    }


}

class SetReal(
    private val name: String,
    private val value: Expr
) : Block(), Command, Property{
    override fun toString(): String {
        return "$name { ${value.eval()} }"
    }

    override fun toGEOJson(d: OutputStream) {
        d.write(", $name: ${value.eval()}".toByteArray())
    }

    override fun getKey(): String = name
    override fun add(m: MutableMap<String, Any>) {
        m[name] = value.eval()
    }

    override fun deepCopy(): Saveable {
        val newVal = Real(value.eval())
        return SetReal(name, newVal)
    }

    override fun save() { return }

    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return true
    }

    override fun evaluate() {
        return
    }
}

class Get(
    private val name: String
) : Expr {
    override fun toString(): String {
        return "Get $name"
    }

    override fun eval(): Double {
        println("GET ${propertyStack.peek()[name]}")
        if(!propertyStack.peek().containsKey(name)) return 0.0
        if(propertyStack.peek()[name] !is Double) return 0.0
        return propertyStack.peek()[name] as Double
    }


}

interface Construct : SuperType{
    override fun toString(): String

    fun toGEOJson(d: OutputStream)
}

class City(
    private val name: String,
    private val blockList: BlockList? = null,
    private val properties: MutableMap<String, Any> = mutableMapOf(),
    private val blocks: MutableList<Block> = mutableListOf()

) : Construct {
    override fun toString(): String {
        return "$name { $blockList }"
    }

    fun eval(blockList: BlockList?){
        propertyStack.push(properties)
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
                block.add(properties)
            }
            else if(block is ForLoop){
                val forResult = block.eval()
                properties.putAll(forResult.first)
                blocks.addAll(forResult.second as MutableList<Block>)
            }
            else if(block is ForEach){
                block.eval(blocks)
            }
            else {
                block = if(block is Variable) block.eval() as Block else block
                val newBlock = (block as Copyable).deepCopy() as Block
                newBlock.evaluate()
                blocks.add(newBlock)
                println("Properties ${newBlock.properties}")

            }
            bl = bl?.blockList
            block = bl?.block
        }
        propertyStack.pop()
    }

    fun printProperties(d: OutputStream){
        properties.forEach{
            d.write(",${it.key}: ".toByteArray())
            if(it.value is Expr){
                d.write("${(it.value as Expr).eval()} ".toByteArray())
            }
            else if(it.value is String){
                d.write("${it.value} ".toByteArray())
            }
            else throw Error("Invalid propery type")
        }
    }
    override fun toGEOJson(d: OutputStream) {
        eval(blockList)

        firstBlock = true;
        d.write("{".toByteArray())
        d.write("\"type\": \"FeatureCollection\",".toByteArray())
        d.write("\"properties\": {".toByteArray())
        d.write("\"name\": $name".toByteArray())
        printProperties(d)
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
        vars.clear()
        vars["nil"] = 0
        construct.toGEOJson(d)
        constructList?.toGEOJson(d)
    }


}

class Assign(
    val v: Variable,
    private val e: Any
) : Construct, Block(), Command, Evaluable {
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

    override fun evaluate() {
        return
    }
}

class Reassign(
    private val v: Variable,
    private val e: Any
) : Construct, Block(), Command, Evaluable {
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

    override fun evaluate() {
        return
    }
}


class Variable(
    private val s: String
) : Element, PointType, Construct, Command, Block() {
    override fun toString(): String {
        return s
    }

    override fun toGEOJson(d: OutputStream) {
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

    override fun evaluate() {
        return
    }


    override fun eval(): Any{
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
abstract class Comparator(
    var e1: Element,
    val e2: Element
){
    abstract fun eval(): Boolean
}
class Greater(
    e1: Element,
    e2: Element
) : Comparator(e1, e2){
    override fun eval(): Boolean {
        return ((e1.eval() as Double) > (e2.eval() as Double))
    }

}

class Lesser(
    e1: Element,
    e2: Element
) : Comparator(e1, e2){
    override fun eval(): Boolean {
        return ((e1.eval() as Double) < (e2.eval() as Double))
    }

}
class Equal(
    e1: Element,
    e2: Element
) : Comparator(e1, e2){
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
    e1: Element,
    e2: Element
) : Comparator(e1, e2){
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
    e1: Element,
    e2: Element
) : Comparator(e1, e2){
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
                if(e1p.isContainedInCircle(p, e2p.r.eval())){
                    return false
                }
            } else throw Error("First parameter of out must be a block")
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
                if(e1p.isContainedInRectangle(p1, p2)){
                    return false
                }
            } else throw Error("First parameter of out must be a block")
        } else throw Error("Second parameter in of out only be simple blocks with area (Box, Circle)")
        return true
    }

}

class If(
    val c: Comparator,
    val b: ObjList?
) : Block(), Command, Construct {
    fun eval(): ObjList? {
        println(c.getClass())
        if(c.eval()){
            return b
        }
        return null
    }

    override fun toString(): String {
        return "if($c) $b"
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

    override fun evaluate() {
        return
    }
}

class ForLoop(
    val a: Assign,
    val e: Expr,
    val b: ObjList?,
    val superTypes: MutableList<SuperType> = mutableListOf()
): Construct, Block(), Command{
    override fun toString(): String {
        return b.toString()
    }

    fun eval(blockList: BlockList?){
        propertyStack.push(properties)
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
                block.add(properties)
            } else {
                val newBlock = (block as Copyable).deepCopy() as Block
                newBlock.evaluate()
                superTypes.add(newBlock)
            }
            bl = bl?.blockList
            block = bl?.block
        }
        propertyStack.pop()
    }
    fun eval(commandList: CommandList?){
        propertyStack.push(properties)
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
                command.add(properties)
            } else {
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                superTypes.add(newCommand)
            }
            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }

    fun eval(): Pair<MutableMap<String, Any>, MutableList<SuperType>> {
        a.eval()
        while ((vars[a.v.toString()] as Double) < e.eval()) {
            if(b is BlockList)
                eval(b)
            if(b is CommandList)
                eval(b)
            vars[a.v.toString()] = (vars[a.v.toString()]!! as Double) + 1
        }

        return Pair(properties, superTypes)
    }

    override fun toGEOJson(d: OutputStream) {}

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

    override fun evaluate() {
        return
    }

}

class ForEach(
    val v: Variable,
    val o: ObjList?
): Block() {
    override fun toString(): String {
        return v.toString()
    }

    override fun toGEOJson(d: OutputStream) {
        return
    }

    fun updateCurrent(e: Any){
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

    fun eval(commandList: CommandList?, properties: MutableMap<String, Any>, commands: MutableList<Command>){
        propertyStack.push(properties)
        var cl = commandList
        var command = cl?.comm
        while (command != null) {
            if(command is Evaluable){
                command.eval()
            }
            else if(command is If){
                println(command)

                val innerList = (command).eval() as CommandList?
                println(innerList)
                eval(innerList, properties, commands)
            }
            else if(command is ForLoop){
                val forResult = command.eval()
                properties.putAll(forResult.first)
                commands.addAll(forResult.second as MutableList<Command>)
            }
            else if(command is Property){
                command.add(properties)
            } else {
                command = if(command is Variable) command.eval() as Command else command
                val newCommand = ((command as Saveable).deepCopy() as Command)
                (newCommand as Saveable).save()
                commands.add(newCommand)
            }

            cl = cl?.comms
            command = cl?.comm
        }
        propertyStack.pop()
    }
    fun eval(blocks: MutableList<Block>){
        blocks.forEach {
            updateCurrent(it)
            println("Properties ${it.properties}")

            println(vars[v.toString()])
            eval(o as CommandList, it.properties, it.commands)

        }
    }
    override fun isContainedInCircle(pc: Point, r: Double): Boolean {
        return true
    }

    override fun isContainedInRectangle(pr1: Point, pr2: Point): Boolean {
        return true
    }

    override fun evaluate() {
        return
    }

}