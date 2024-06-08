package org.example

import kotlin.Boolean

object Auiks {
    fun isPointInCircle(x: Double, y: Double, centerX: Double, centerY: Double, radius: Double): Boolean {
        val distance = Math.sqrt(Math.pow(x - centerX, 2.0) + Math.pow(y - centerY, 2.0))
        return distance <= radius
    }

    fun isPointInRectangle(x: Double, y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Boolean {
        return x in minOf(x1, x2)..maxOf(x1, x2) && y in minOf(y1, y2)..maxOf(y1, y2)
    }
}

