package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*
import top.goopper.platform.pojo.UserFullDetails

@Component
class UserDAO(private val database: Database) {

    fun loadUserWithPasswordByUserNumber(number: Long): UserFullDetails {
        val rows = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }.iterator()
        if (rows.hasNext()) {
            val row = rows.next()
            if (row[User.enable] == false) {
                throw Exception("User is disabled")
            }
            return UserFullDetails(
                raw = processUserDTO(row),
                encodedPassword = row[User.password]!!
            )
        } else {
            throw Exception("User does not exist or password is incorrect")
        }
    }

    fun loadUserByNumber(number: Long): UserDTO {
        val rows = database.from(User)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                User.number eq number
            }.iterator()
        return processUserResult(rows)
    }

    fun loadUserByOAuthId(githubID: String): UserDTO {
        val rows = database.from(OAuthUser)
            .innerJoin(User, OAuthUser.userId eq User.id)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                OAuthUser.oauthId eq githubID
            }.iterator()
        return processUserResult(rows)
    }

    fun processUserResult(rows: Iterator<QueryRowSet>): UserDTO {
        if (rows.hasNext()) {
            val row = rows.next()
            if (row[User.enable] == false) {
                throw Exception("User is disabled")
            }
            return processUserDTO(row)
        } else {
            throw Exception("User does not exist or password is incorrect")
        }
    }

    fun processUserDTO(row: QueryRowSet): UserDTO = UserDTO(
        id = row[User.id]!!,
        name = row[User.name]!!,
        email = row[User.email]!!,
        avatar = row[User.avatar]!!,
        number = row[User.number]!!,
        roleId = row[User.roleId]!!,
        groupId = row[User.groupId]!!,
        roleName = row[Role.name]!!,
        groupName = row[Group.name]!!
    )

    fun bindUserWithOAuth(id: Long, oauthId: String, oauthName: String, providerName: String): Boolean {
        val providerId = database.from(OAuthProvider)
            .select()
            .where{ OAuthProvider.name eq providerName }
            .iterator().next()[OAuthProvider.id]
        if (providerId == null) {
            throw Exception("OAuth provider does not exist")
        }
        val count = database.insert(OAuthUser) {
            set(OAuthUser.userId, id)
            set(OAuthUser.oauthId, oauthId)
            set(OAuthUser.oauthName, oauthName)
            set(OAuthUser.providerId, providerId)
        }
        return count == 1
    }

}