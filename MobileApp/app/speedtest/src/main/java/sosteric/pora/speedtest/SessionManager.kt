
class SessionManager {
    var isSet: Boolean = false
    var token: String? = null
    var user: User? = null

    fun start(pair: Pair<String, User>?) : Boolean {
        if(pair != null){
            isSet = true
            token = pair.first
            user = pair.second
            return true
        }
        return false
    }

    fun destroy() {
        isSet = false
        token = null
        user = null
    }

    fun isLoggedIn() : Boolean {
        return isSet
    }

    override fun toString(): String {
        return "$isSet $token $user"
    }
}