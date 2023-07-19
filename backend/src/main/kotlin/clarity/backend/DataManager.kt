package clarity.backend

import java.io.File
import java.nio.charset.Charset
import java.sql.Connection
import java.sql.DriverManager
class DataManager {

    companion object {
        private var db: Connection? = null;

        fun conn(): Connection? {
            if(db == null) {
                Class.forName("org.sqlite.JDBC");
                db = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
                return db;
            }

            return db
        }
    }
    fun createTables() {
        createDbFile()
        executeSqlFile("ddl/CREATE_USER.sql");
        executeSqlFile("ddl/CREATE_CLASSROOM.sql")
        executeSqlFile("ddl/CREATE_CLASSROOM_STUDENTS.sql")
        executeSqlFile("ddl/CREATE_CARDSET.sql")
        executeSqlFile("ddl/CREATE_CARD.sql")
        executeSqlFile("ddl/CREATE_CARD_IN_SET.sql")
        executeSqlFile("ddl/CREATE_TASKS.sql")
        executeSqlFile("ddl/CREATE_ATTEMPTS.sql")
        executeSqlFile("ddl/CREATE_CLASSROOM_ATTEMPTS.sql")
        executeSqlFile("ddl/CREATE_ANNOUNCEMENTS.sql")
        executeSqlFile("ddl/CREATE_FOLLOWING.sql")
        executeSqlFile("ddl/CREATE_SETLIKES.sql")
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
    private fun executeSqlFile(pathInsideSQLFolder: String) {
        val sqlFile = File("src/main/sql/$pathInsideSQLFolder")
        if(sqlFile.exists()) {
            try {
                val contents = sqlFile.readText(Charset.defaultCharset());
                val statement = conn()?.createStatement()
                statement?.executeUpdate(contents)
                statement?.close()
            } catch (e: Exception) {
                e.printStackTrace();
            }

        } else {
            println("${sqlFile.absolutePath} does not exist. Please fix your file path");
        }
    }
}