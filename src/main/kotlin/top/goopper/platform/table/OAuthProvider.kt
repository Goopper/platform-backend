package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object OAuthProvider : Table<Nothing>("oauth_provider") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val createTime = datetime("create_time")
}