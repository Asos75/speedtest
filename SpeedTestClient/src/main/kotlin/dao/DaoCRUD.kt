package dao

import org.bson.types.ObjectId
interface DaoCrud<T> {
    fun getById(id: ObjectId): T?
    fun getAll(): List<T>
    fun insert(obj: T): Boolean
    fun update(obj: T): Boolean
    fun delete(obj: T): Boolean

}