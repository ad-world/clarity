package clarity.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClarityBackend

fun main(args: Array<String>) {
    DataManager().createTables();
    runApplication<ClarityBackend>(*args)
}
