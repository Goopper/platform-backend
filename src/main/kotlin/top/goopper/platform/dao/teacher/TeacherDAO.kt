package top.goopper.platform.dao.teacher

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.teacher.TeacherListDTO
import top.goopper.platform.enum.RoleEnum
import top.goopper.platform.table.User

@Repository
class TeacherDAO(
    private val database: Database
) {

    fun getTeacherList(): List<TeacherListDTO> {
        val teacher = database.from(User)
            .select()
            .where { User.roleId eq RoleEnum.TEACHER.id }
            .map {
                TeacherListDTO(
                    id = it[User.id]!!,
                    name = it[User.name]!!
                )
            }
        return teacher
    }

}