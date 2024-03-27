package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.entity.*
import top.goopper.platform.utils.DTOUtils.Companion.processUserDTO
import java.time.LocalDateTime

@Component
class OAuthDAO(
    private val database: Database,
    private val providerDAO: ProviderDAO
) {

    /**
     * Bind user with OAuth.
     * Because of the multiple duplicate index, use manual insertOrUpdate instead of org.ktorm.support.mysql.insertOrUpdate
     * @return true if bind success
     * @throws Exception if OAuth provider does not exist
     */
    fun bindUserWithOAuth(
        id: Long,
        oauthId: String,
        oauthName: String,
        providerName: String,
        isRebind: Boolean
    ): Boolean {
        val providerId = providerDAO.loadProviderByProviderName(providerName)
        // effect row count
        val count: Int
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

    /**
     * Load user by OAuth. If user not found, throw exception.
     */
    fun loadUserByOAuth(oauthId: String, providerName: String): UserDTO {
        val providerId = providerDAO.loadProviderByProviderName(providerName)
        val rows = database.from(OAuthUser)
            .innerJoin(OAuthProvider, OAuthUser.providerId eq OAuthProvider.id)
            .innerJoin(User, OAuthUser.userId eq User.id)
            .innerJoin(Role, User.roleId eq Role.id)
            .innerJoin(Group, User.groupId eq Group.id)
            .select()
            .where {
                OAuthUser.oauthId eq oauthId
                OAuthUser.providerId eq providerId
            }.iterator()
        return processUserDTO(rows)
    }
}