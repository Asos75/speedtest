import org.bson.types.ObjectId

class MobileTower(
    var location: Location,
    var provider: String,
    var type: String,
    var confirmed: Boolean,
    var locator: User?,
    var id: ObjectId = ObjectId()
) {
    override fun toString(): String {
        return "${location}, $provider, $type, $confirmed, $locator $id"
    }
}