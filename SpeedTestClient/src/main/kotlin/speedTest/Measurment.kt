package speedTest

import org.bson.types.ObjectId
import util.Location
import java.time.LocalDateTime

class Measurment (
    var speed: Long,
    var type: Type,
    var operator: String,
    var location: Location,
    var time: LocalDateTime,
    var userId: ObjectId?
){
    override fun toString(): String {
        return "$speed, $type, $operator, ${location.coordinates[0]}, ${location.coordinates[1]}, $time, $userId"
    }
}