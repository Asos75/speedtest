import android.os.Parcel
import android.os.Parcelable
import org.bson.types.ObjectId

class User(
    var username: String,
    val password: String,
    val email: String,
    val admin: Boolean = false,
    val id: ObjectId = ObjectId()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readSerializable() as ObjectId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(email)
        parcel.writeByte(if (admin) 1 else 0)
        parcel.writeSerializable(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "$id $username"
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}