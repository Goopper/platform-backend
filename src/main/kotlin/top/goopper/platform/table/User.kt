package top.goopper.platform.table

import org.ktorm.schema.*

object User : Table<Nothing>("user") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    val password = varchar("password")
    val avatar = varchar("avatar")
    val sex = boolean("sex")
    val number = long("number")
    val roleId = long("role_id")
    val groupId = long("group_id")
    val enable = boolean("enable")
    val accountNonExpired = boolean("account_non_expired")
    val accountNonLocked = boolean("account_non_locked")
    val credentialsNonExpired = boolean("credentials_non_expired")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
}