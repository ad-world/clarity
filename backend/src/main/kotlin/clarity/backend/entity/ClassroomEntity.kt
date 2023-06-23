package clarity.backend.entity

import clarity.backend.DataManager

data class JoinClassroomEntity(val privateCode: String, val username: String, val firstname: String, val lastname: String)
data class CreateClassroomEntity(val name: String,)

class ClassroomEntity(dataManager: DataManager) {
    private val db = dataManager.db

    fun joinClass(classroom : JoinClassroomEntity) {
        try {
            val statement = db.createStatement()

        }
    }

    fun createClass(classroomEntity: CreateClassroomEntity) {
        try {
            val statement = db.createStatement()

        }
    }
}