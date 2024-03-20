package top.goopper.platform.entity

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Group : Table<Nothing>("group") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val teacherId = long("teacher_id")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")
}