import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dao.mongodb.MongoUser
import util.*
import kotlin.random.Random
import org.bson.types.ObjectId
import speedTest.Type
import util.GeneratorUtil.generateToMongo

suspend fun main(){
    var conn : MongoDatabase? = DatabaseUtil.setupConnection()
    if (conn != null) {
        DatabaseUtil.listAllCollection(conn).collect{ println(it) }
    }

    val mongoUser = MongoUser(conn)
    val kotlinTest = User("KotlinTester", "admin")
    println( mongoUser.authenticate(kotlinTest))
}
