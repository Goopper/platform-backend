package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*
import top.goopper.platform.pojo.UserFullDetails
import top.goopper.platform.utils.DTOUtils.Companion.processUserDTO

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
        return UserFullDetails(
            raw = processUserDTO(rows),
            encodedPassword = rows.next()[User.password]!!
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
        return processUserDTO(rows)
    }

    fun loadUserByOAuth(githubID: String): UserDTO {
        val rows = database.from(OAuthUser)
            .innerJoin(User, OAuthUser.userId eq User.id)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .innerJoin(OAuthProvider, OAuthUser.providerId eq OAuthProvider.id)
            .select()
            .where {
                OAuthUser.oauthId eq githubID
            }.iterator()
        return processUserDTO(rows)
    }

}