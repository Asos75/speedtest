package org.example

import kotlin.Boolean
import kotlin.math.*

object Auiks {
    fun isPointInCircle(lat1: Double, lon1: Double, lat2: Double, lon2: Double, radius: Double): Boolean {
        // Radius of the Earth in kilometers
        val R = 6371.0

        // Convert latitude and longitude from degrees to radians
        val lat1Rad = Math.toRadians(lat1)
        val lon1Rad = Math.toRadians(lon1)
        val lat2Rad = Math.toRadians(lat2)
        val lon2Rad = Math.toRadians(lon2)

        // Compute the differences between the coordinates
        val deltaLat = lat2Rad - lat1Rad
        val deltaLon = lon2Rad - lon1Rad

        // Apply the Haversine formula
        val a = sin(deltaLat / 2).pow(2.0) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c

        // Check if the distance is within the radius
        return distance <= radius
    }

    fun isPointInRectangle(x: Double, y: Double, x1: Double, y1: Double, x2: Double, y2: Double): Boolean {
        return x in minOf(x1, x2)..maxOf(x1, x2) && y in minOf(y1, y2)..maxOf(y1, y2)
    }
}