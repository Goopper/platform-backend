package top.goopper.platform.utils

import org.ktorm.dsl.QueryRowSet
import top.goopper.platform.dto.OAuthDTO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*

class DTOUtils {
    companion object {
        fun processUserDTO(row: QueryRowSet): UserDTO {
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
                avatar = row[User.avatar]!!,
                sex = row[User.sex]!!
            )
        }

        fun processUserDTOByRows(rows: Iterator<QueryRowSet>): UserDTO {
            if (!rows.hasNext())
                throw Exception("User not found")
            val row = rows.next()
            return processUserDTO(row)
        }

        // Providers to OAuthDTO, false if user not bind this provider
        fun processOAuthDTOByRows(rows: Iterator<QueryRowSet>): List<OAuthDTO> {
            val list = mutableListOf<OAuthDTO>()
            while (rows.hasNext()) {
                val row = rows.next()
                list.add(OAuthDTO(
                    id = row[OAuthProvider.id]!!,
                    name = row[OAuthProvider.name]!!,
                    bind = row[OAuthUser.oauthId] != null,
                    bindUsername = row[OAuthUser.oauthName],
                    bindId = row[OAuthUser.oauthId]
                ))
            }
            return list
        }
    }
}