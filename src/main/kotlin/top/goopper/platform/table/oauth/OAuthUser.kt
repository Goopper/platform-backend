package top.goopper.platform.table.oauth

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object OAuthUser : Table<Nothing>("oauth_user") {
    val id = int("id").primaryKey()
    val userId = int("user_id")
    val providerId = int("provider_id")
    val oauthId = varchar("oauth_id")
    val oauthName = varchar("oauth_name")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
}