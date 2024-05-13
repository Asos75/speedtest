import java.time.LocalDateTime

class Event(
    var name: String,
    var type: String,
    var time: LocalDateTime,
    var online: Boolean,
    var location: Location?,
) {
    override fun toString(): String {
        return "$name, $type, $time, $online, $location"
    }
}