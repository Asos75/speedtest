import org.bson.types.ObjectId

class User (
    var username: String,
    val password: String,
    val email: String,
    val admin: Boolean = false,
    val id: ObjectId = ObjectId(),
) {

    override fun toString(): String {
        return  "$id $username"
    }

}