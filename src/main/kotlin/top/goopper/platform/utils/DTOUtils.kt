package top.goopper.platform.utils

import org.ktorm.dsl.QueryRowSet
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.Group
import top.goopper.platform.entity.Role
import top.goopper.platform.entity.User

class DTOUtils {
    companion object {
        fun processUserDTO(rows: Iterator<QueryRowSet>): UserDTO {
            if (!rows.hasNext())
                throw Exception("User not found")
            val row = rows.next()
            if (row[User.enable] == false)
                throw Exception("User is disabled")
            return UserDTO(
                id = row[User.id]!!,
                number = row[User.number]!!,
                name = row[User.name]!!,
                roleId = row[User.roleId]!!,
                roleName = row[Role.name]!!,
                groupId = row[User.groupId]!!,
                groupName = row[Group.name]!!,
                email = row[User.email]!!,
                avatar = row[User.avatar]!!
            )
        }
    }
}