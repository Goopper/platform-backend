package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.support.mysql.insertOrUpdate
import org.springframework.stereotype.Component
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*
import top.goopper.platform.pojo.UserFullDetails
import java.time.LocalDateTime

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

    /**
     * Bind user with OAuth.
     * Because of the multiple duplicate index, use manual insertOrUpdate instead of org.ktorm.support.mysql.insertOrUpdate
     * @param id user id
     * @param oauthId oauth id
     * @param oauthName oauth name
     * @param providerName provider name
     * @return true if bind success
     * @throws Exception if OAuth provider does not exist
     */
    fun bindUserWithOAuth(id: Long, oauthId: String, oauthName: String, providerName: String): Boolean {
        val providerId = database.from(OAuthProvider)
            .select()
            .where{ OAuthProvider.name eq providerName }
            .iterator().next()[OAuthProvider.id]
        if (providerId == null) {
            throw Exception("OAuth provider does not exist")
        }
        var isRebind = false
        // effect row count
        val count: Int
        database.from(OAuthUser)
            .select()
            .where {
                OAuthUser.userId eq id
                OAuthUser.providerId eq providerId
            }.iterator().asSequence().forEach { _ ->
                isRebind = true
            }
        if (isRebind) {
            // update oauth info
            count = database.update(OAuthUser) {
                set(OAuthUser.oauthId, oauthId)
                set(OAuthUser.oauthName, oauthName)
                set(OAuthUser.modifyTime, LocalDateTime.now())
                where {
                    OAuthUser.userId eq id
                    OAuthUser.providerId eq providerId
                }
            }
        } else {
            // insert oauth info
            count = database.insert(OAuthUser) {
                set(OAuthUser.userId, id)
                set(OAuthUser.providerId, providerId)
                set(OAuthUser.oauthId, oauthId)
                set(OAuthUser.oauthName, oauthName)
            }
        }
        // return true if effect row count > 0
        return count > 0
    }

}