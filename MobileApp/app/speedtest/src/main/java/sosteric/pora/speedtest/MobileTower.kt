import android.os.Parcel
import android.os.Parcelable
import org.bson.types.ObjectId

class MobileTower(
    var location: Location,
    var provider: String,
    var type: String,
    var confirmed: Boolean,
    var locator: User?,
    var id: ObjectId = ObjectId()
) : Parcelable {
    override fun toString(): String {
        return "${location}, $provider, $type, $confirmed, $locator $id"
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(location, flags)
        parcel.writeString(provider)
        parcel.writeString(type)
        parcel.writeByte(if (confirmed) 1 else 0)
        parcel.writeParcelable(locator, flags)
        parcel.writeSerializable(id)
    }

    class MobileTowerAlt(
        var location: Location,
        var provider: String,
        var type: String,
        var confirmed: Boolean,
        var locator: String?,
        var id: ObjectId
    )

    fun toAlt(): MobileTowerAlt {
        val currentLocator = locator
        val locatorId = if(locator != null) { currentLocator!!.id.toString() } else { null }
        return MobileTowerAlt(location, provider, type, confirmed, locatorId, id)
    }
}