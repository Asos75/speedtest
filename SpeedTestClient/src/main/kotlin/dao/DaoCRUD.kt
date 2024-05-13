package dao

import org.bson.types.ObjectId
interface DaoCrud<T> {
    suspend fun getById(id: ObjectId): T?
    suspend fun getAll(): List<T>
    suspend fun insert(obj: T): Boolean
    suspend fun update(obj: T): Boolean
    suspend fun delete(obj: T): Boolean

}