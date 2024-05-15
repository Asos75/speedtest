class SessionManager {
    var isSet = false
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

    override fun toString(): String {
        return "$isSet $token $user"
    }
}