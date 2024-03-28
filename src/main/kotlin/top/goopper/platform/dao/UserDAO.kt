package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*
import top.goopper.platform.pojo.UserFullDetails
import top.goopper.platform.utils.DTOUtils.Companion.processUserDTO
import top.goopper.platform.utils.DTOUtils.Companion.processUserDTOByRows

@Component
class UserDAO(private val database: Database) {

    fun loadFullUserByUserNumber(number: Long): UserFullDetails {
        val rows = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }.iterator()
        if (!rows.hasNext())
            throw Exception("User not found")
        val row = rows.next()
        return UserFullDetails(
            raw = processUserDTO(row),
            encodedPassword = row[User.password]!!
        )
    }

    fun loadUserByNumber(number: Long): UserDTO {
        val rows = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }.iterator()
        return processUserDTOByRows(rows)
    }

    fun loadUserById(id: Long): UserDTO {
        val rows = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.id eq id
            }.iterator()
        return processUserDTOByRows(rows)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun updateEmail(id: Long, new: String) {
        database.update(User) {
            set(User.email, new)
            where {
                User.id eq id
            }
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun updatePassword(id: Long, encode: String?) {
        database.update(User) {
            set(User.password, encode)
            where {
                User.id eq id
            }
        }
    }

}