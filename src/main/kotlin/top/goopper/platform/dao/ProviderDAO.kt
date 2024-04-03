package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import top.goopper.platform.dto.OAuthDTO
import top.goopper.platform.table.OAuthProvider
import top.goopper.platform.table.OAuthUser
import top.goopper.platform.utils.DTOUtils.Companion.processOAuthDTOByRows

@Component
class ProviderDAO(private val database: Database) {

    fun loadProviderByProviderName(providerName: String): Long = database.from(OAuthProvider)
        .select()
        .where { OAuthProvider.name eq providerName }
        .iterator().next()[OAuthProvider.id] ?: throw Exception("OAuth provider does not exist")

    fun loadOAuthBindingList(id: Long): List<OAuthDTO> {
        val providerRows = database.from(OAuthProvider)
            .leftJoin(OAuthUser, OAuthUser.providerId eq OAuthProvider.id)
            .select()
            .where {
                (OAuthUser.userId eq id) or (OAuthUser.userId.isNull())
            }.iterator()
        return processOAuthDTOByRows(providerRows)
    }

}