import org.bson.types.ObjectId

class User (
    val id: ObjectId,
    val username: String,
    val password: String,
    //TODO add mail
) {

    override fun toString(): String {
        return  "$id $username"
    }

}