package sosteric.pora.speedtest

import Location
import User
import java.time.LocalDateTime

class ExtremeEvent(
    val type: Events,
    val location: Location,
    val time: LocalDateTime,
    val user: User?
) {

    override fun toString(): String {
        return "$type, $location, $time, $user"
    }

    class ExtremeEventAlt(
        val type: String,
        val location: Location,
        val time: LocalDateTime,
        val user: String?
    )

    fun toAlt(): ExtremeEventAlt {
        val currentUser = user
        val userId = if(user != null) { currentUser!!.id.toString() } else { null }
        return ExtremeEventAlt(type.toString(), location, time, userId)
    }
}