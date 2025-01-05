import org.bson.types.ObjectId
import sosteric.pora.speedtest.Type
import java.time.LocalDateTime
import android.os.Parcel
import android.os.Parcelable

class Measurment (
    var speed: Long,
    var type: Type,
    var provider: String,
    var location: Location,
    var time: LocalDateTime,
    var user: User?,
    val id: ObjectId = ObjectId()
): Parcelable {
    override fun toString(): String {
        return "$speed, $type, $provider, ${location.coordinates[0]}, ${location.coordinates[1]}, $time, $user"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(speed)
        parcel.writeString(type.name)
        parcel.writeString(provider)
        parcel.writeParcelable(location, flags)
        parcel.writeSerializable(time)
        parcel.writeParcelable(user, flags)
        parcel.writeSerializable(id)
    }

    class MeasurementAlt(
        var speed: Long,
        var type: Type,
        var provider: String,
        var location: Location,
        var time: LocalDateTime,
        var user: String?,
        val id: ObjectId
    )

    fun toAlt(): MeasurementAlt {
        val currentUser = user
        val userId = if(user != null) { currentUser!!.id.toString() } else { null }
        return MeasurementAlt(speed, type, provider, location, time, userId , id)
    }
}