package dao

import Measurment
import User
import java.time.LocalDateTime

interface MeasurementCrud : DaoCrud<Measurment> {
    suspend fun getByUser(user: User): List<Measurment>
    suspend fun getByTimeFrame(start: LocalDateTime, end: LocalDateTime): List<Measurment>
}