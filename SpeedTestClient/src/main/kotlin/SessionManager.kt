import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SessionManager {
    var isSet by mutableStateOf(false)
    var token by mutableStateOf<String?>(null)
    var user by mutableStateOf<User?>(null)
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