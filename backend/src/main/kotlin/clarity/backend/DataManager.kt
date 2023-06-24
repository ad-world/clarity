package clarity.backend

import java.io.File
import java.nio.charset.Charset
import java.sql.Connection
import java.sql.DriverManager
class DataManager {
    val db: Connection = DriverManager.getConnection("jdbc:sqlite:data.sqlite")

    init {
        createDbFile()
        createSampleTable();
    }

    private fun createSampleTable() {
        executeSqlFile("ddl/CREATE_USER.sql");
        executeSqlFile("ddl/CREATE_CLASSROOM.sql")
    }

    private fun createDbFile() {
        val file = File("data.sqlite")
        if(!file.exists()) {
            val success = file.createNewFile()
            println(success)
        }
    }

    /*
    Use this function if you want to run one sql statement from a file (such as a CREATE TABLE statement)
     */
    fun executeSqlFile(pathInsideSQLFolder: String) {
        val sqlFile = File("src/main/sql/$pathInsideSQLFolder")
        if(sqlFile.exists()) {
            try {
                val contents = sqlFile.readText(Charset.defaultCharset());
                val statement = db.createStatement()
                statement.executeUpdate(contents)
                statement.close()
            } catch (e: Exception) {
                e.printStackTrace();
            }

        } else {
            println("${sqlFile.absolutePath} does not exist. Please fix your file path");
        }
    }
}