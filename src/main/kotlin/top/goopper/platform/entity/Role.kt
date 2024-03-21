package top.goopper.platform.entity

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Role : Table<Nothing>("role") {
    val id = long("id").primaryKey()
    val name = varchar("name")
    val createTime = datetime("create_time")
}