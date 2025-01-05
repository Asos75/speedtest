import org.bson.types.ObjectId
import sosteric.pora.speedtest.Type
import java.time.LocalDateTime

class Measurment (
    var speed: Long,
    var type: Type,
    var provider: String,
    var location: Location,
    var time: LocalDateTime,
    var user: User?,
    val id: ObjectId = ObjectId()
){
    override fun toString(): String {
        return "$speed, $type, $provider, ${location.coordinates[0]}, ${location.coordinates[1]}, $time, $user"
    }
}