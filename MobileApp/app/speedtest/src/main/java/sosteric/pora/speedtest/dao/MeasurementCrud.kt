package dao

import Measurment
import User
import java.time.LocalDateTime

interface MeasurementCrud : DaoCrud<Measurment> {
    fun getByUser(user: User): List<Measurment>
    fun getByTimeFrame(start: LocalDateTime, end: LocalDateTime): List<Measurment>
    fun insertMany(list: List<Measurment>): Boolean
}