package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.springframework.stereotype.Component
import top.goopper.platform.entity.OAuthProvider

@Component
class ProviderDAO(private val database: Database) {

    fun loadProviderByProviderName(providerName: String): Long = database.from(OAuthProvider)
        .select()
        .where { OAuthProvider.name eq providerName }
        .iterator().next()[OAuthProvider.id] ?: throw Exception("OAuth provider does not exist")

}