package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

open class Group(alias: String?) : Table<Nothing>("group", alias) {
    companion object : Group(null)

    val id = int("id").primaryKey()
    val name = varchar("name")
    val teacherId = int("teacher_id")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

    override fun aliased(alias: String) = Group(alias)
}