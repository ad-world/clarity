import org.springframework.stereotype.Component
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Component
class TemporaryFileStorage {
    private val tempDir: Path = Files.createTempDirectory("temp-files")

    fun storeFile(multipartFile: MultipartFile): String {
        val fileName = multipartFile.originalFilename ?: throw IllegalArgumentException("Invalid file name")
        val targetPath = tempDir.resolve(fileName)

        try {
            Files.copy(multipartFile.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
            return targetPath.toString()
        } catch (e: IOException) {
            throw RuntimeException("Failed to store file: $fileName", e)
        }
    }

    fun deleteFile(filePath: String) {
        try {
            val pathToDelete = Path.of(filePath)
            Files.deleteIfExists(pathToDelete)
        } catch (e: IOException) {
            throw RuntimeException("Failed to delete file: $filePath", e)
        }
    }

    fun deleteAllFiles() {
        FileSystemUtils.deleteRecursively(tempDir)
    }
}