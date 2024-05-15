import org.bson.types.ObjectId
import java.time.LocalDateTime

class Event(
    var name: String,
    var type: String,
    var time: LocalDateTime,
    var online: Boolean,
    var location: Location?,
    val id: ObjectId = ObjectId()
) {
    override fun toString(): String {
        return "$name, $type, $time, $online, $location"
    }
}