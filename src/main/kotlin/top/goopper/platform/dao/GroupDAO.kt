package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.GroupDTO
import top.goopper.platform.entity.Group

@Component
class GroupDAO(private val database: Database) {

    fun getByTeacherId(id: Long): List<GroupDTO> {
        val groups = database.from(Group)
            .select()
            .where {
                Group.teacherId eq id
            }.map {
                GroupDTO(
                    id = it[Group.id]!!,
                    name = it[Group.name]!!
                )
            }
        return groups
    }

}