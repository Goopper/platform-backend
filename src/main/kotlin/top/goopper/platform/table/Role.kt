package top.goopper.platform.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Role : Table<Nothing>("role") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val createTime = datetime("create_time")
}