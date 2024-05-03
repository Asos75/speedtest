package speedTest

import util.Location
import java.time.LocalDateTime

class Measurment (
    var speed: Long,
    var type: Type,
    var operator: String,
    var location: Location,
    var time: LocalDateTime,
    var userId: Int
){

}