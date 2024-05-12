package dao

import User

interface UserCRUD : DaoCrud<User> {
    suspend fun authenticate(obj: User): Boolean
}