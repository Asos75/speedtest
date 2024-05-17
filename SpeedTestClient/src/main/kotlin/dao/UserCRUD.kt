package dao

import User

interface UserCRUD : DaoCrud<User> {
    fun authenticate(username: String, password: String): Boolean
}