package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.UserFullDetails
import top.goopper.platform.table.Group
import top.goopper.platform.table.Role
import top.goopper.platform.table.User

// TODO: optimize the code
@Repository
class UserDAO(private val database: Database) {

    fun loadFullUserByUserNumber(number: Int): UserFullDetails {
        val result = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .leftJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }
            .map {
                UserFullDetails(
                    raw = UserDTO(
                        id = it[User.id]!!,
                        number = it[User.number]!!,
                        name = it[User.name]!!,
                        roleId = it[User.roleId]!!,
                        roleName = it[Role.name]!!,
                        groupId = it[User.groupId],
                        groupName = it[Group.name],
                        email = it[User.email]!!,
                        avatar = it[User.avatar]!!,
                        sex = it[User.sex]!!
                    ),
                    encodedPassword = it[User.password]!!
                )
            }.firstOrNull() ?: throw Exception("User not found")
        return result
    }

    fun loadUserByNumber(number: Int): UserDTO {
        val result = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .leftJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }
            .map {
                UserDTO(
                    id = it[User.id]!!,
                    number = it[User.number]!!,
                    name = it[User.name]!!,
                    roleId = it[User.roleId]!!,
                    roleName = it[Role.name]!!,
                    groupId = it[User.groupId],
                    groupName = it[Group.name],
                    email = it[User.email]!!,
                    avatar = it[User.avatar]!!,
                    sex = it[User.sex]!!
                )
            }.firstOrNull() ?: throw Exception("User not found")
        return result
    }

    fun loadUserById(id: Int): UserDTO {
        val result = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .leftJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.id eq id
            }
            .map {
                UserDTO(
                    id = it[User.id]!!,
                    number = it[User.number]!!,
                    name = it[User.name]!!,
                    roleId = it[User.roleId]!!,
                    roleName = it[Role.name]!!,
                    groupId = it[User.groupId],
                    groupName = it[Group.name],
                    email = it[User.email]!!,
                    avatar = it[User.avatar]!!,
                    sex = it[User.sex]!!
                )
            }.firstOrNull() ?: throw Exception("User not found")
        return result
    }

    fun updateEmail(id: Int, new: String) {
        database.update(User) {
            set(User.email, new)
            where {
                User.id eq id
            }
        }
    }

    fun updatePassword(id: Int, encode: String?) {
        database.update(User) {
            set(User.password, encode)
            where {
                User.id eq id
            }
        }
    }

    fun updateAvatar(id: Int, url: String) {
        database.update(User) {
            set(User.avatar, url)
            where {
                User.id eq id
            }
        }
    }

}