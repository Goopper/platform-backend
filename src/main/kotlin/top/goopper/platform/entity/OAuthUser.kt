package top.goopper.platform.entity

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object OAuthUser : Table<Nothing>("oauth_user") {
    val id = long("id").primaryKey()
    val userId = long("user_id")
    val providerId = long("provider_id")
    val oauthId = varchar("oauth_id")
    val oauthName = varchar("oauth_name")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
}