package util

import org.mindrot.jbcrypt.BCrypt

object HashingUtil {
    fun hashPassword(password: String): String {
        val saltRounds = 10
        return BCrypt.hashpw(password, BCrypt.gensalt(saltRounds))
    }
}