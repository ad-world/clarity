package clarity.backend

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class DataManager {
    private val db = DriverManager.getConnection("jdbc:sqlite:data.sqlite")

    init {
        createDbFile()
        createSampleTable();
    }

    private fun createSampleTable() {
        try {
            val statement = db.createStatement()
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Data (id integer, name varchar(255))")
            statement.executeUpdate("INSERT INTO Data VALUES(1, 'Aryaman')")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createDbFile() {
        val file = File("data.sqlite")
        if(!file.exists()) {
            val success = file.createNewFile()
            println(success)
        }
    }

    fun conn(): Connection {
        return db
    }

}