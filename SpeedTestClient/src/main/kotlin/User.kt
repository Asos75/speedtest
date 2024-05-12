import org.bson.types.ObjectId

class User (
    var username: String,
    val password: String,
    val id: ObjectId = ObjectId(),
    //TODO add mail
) {

    override fun toString(): String {
        return  "$id $username"
    }

}