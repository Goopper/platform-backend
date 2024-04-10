package top.goopper.platform.dao.oauth

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.OAuthDTO
import top.goopper.platform.table.oauth.OAuthProvider
import top.goopper.platform.table.oauth.OAuthUser

@Repository
class ProviderDAO(private val database: Database) {

    fun loadProviderByProviderName(providerName: String): Int = database.from(OAuthProvider)
        .select()
        .where { OAuthProvider.name eq providerName }
        .map { it[OAuthProvider.id]!! }
        .firstOrNull() ?: throw Exception("OAuth provider does not exist")

    fun loadOAuthBindingList(id: Int): List<OAuthDTO> {
        val result = database.from(OAuthProvider)
            .leftJoin(OAuthUser, OAuthUser.providerId eq OAuthProvider.id)
            .select()
            .where {
                (OAuthUser.userId eq id) or (OAuthUser.userId.isNull())
            }
            .map {
                OAuthDTO(
                    id = it[OAuthProvider.id]!!,
                    name = it[OAuthProvider.name]!!,
                    bind = it[OAuthUser.oauthId] != null,
                    bindUsername = it[OAuthUser.oauthName],
                    bindId = it[OAuthUser.oauthId]
                )
            }
        return result
    }

}