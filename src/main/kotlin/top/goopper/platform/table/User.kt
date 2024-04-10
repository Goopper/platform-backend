package top.goopper.platform.table

import org.ktorm.schema.*

open class User(alias: String?) : Table<Nothing>("user", alias) {
    companion object : User(null)

    val id = int("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    val password = varchar("password")
    val avatar = varchar("avatar")
    val sex = boolean("sex")
    val number = int("number")
    val roleId = int("role_id")
    val groupId = int("group_id")
    val enable = boolean("enable")
    val accountNonExpired = boolean("account_non_expired")
    val accountNonLocked = boolean("account_non_locked")
    val credentialsNonExpired = boolean("credentials_non_expired")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

    override fun aliased(alias: String) = User(alias)
}