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