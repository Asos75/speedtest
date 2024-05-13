import com.mongodb.kotlin.client.coroutine.MongoDatabase
import util.*
import kotlin.random.Random

suspend fun main(){
    var conn : MongoDatabase? = DatabaseUtil.setupConnection()
    if (conn != null) {
        DatabaseUtil.listAllCollection(conn).collect{ println(it) }
    }
}