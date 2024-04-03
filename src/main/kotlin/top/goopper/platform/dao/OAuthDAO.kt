package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.table.*
import top.goopper.platform.utils.DTOUtils.Companion.processUserDTOByRows
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
     * @throws DuplicateKeyException if OAuth binding already exists
     */
    @Transactional(rollbackFor = [Exception::class])
    fun bindUserWithOAuth(
        id: Long,
        oauthId: String,
        oauthName: String,
        providerName: String,
        isRebind: Boolean
    ) {
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
                    (OAuthUser.userId eq id) and (OAuthUser.providerId eq providerId)
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
        if (count != 1) {
            throw Exception("OAuth binding failed")
        }
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
                (OAuthUser.oauthId eq oauthId) and (OAuthUser.providerId eq providerId)
            }.iterator()
        return processUserDTOByRows(rows)
    }

    /**
     * Unbind user with OAuth. If record not found, return false.
     */
    @Transactional(rollbackFor = [Exception::class])
    fun unbindUserWithOAuth(id: Long, providerName: String) {
        val providerId = providerDAO.loadProviderByProviderName(providerName)
        val count = database.delete(OAuthUser) {
            (it.providerId eq providerId) and (it.userId eq id)
        }
        if (count != 1) {
            throw Exception("OAuth unbinding filed")
        }
    }
}