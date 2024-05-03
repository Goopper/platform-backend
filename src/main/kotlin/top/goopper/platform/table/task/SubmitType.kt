package top.goopper.platform.table.task

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object SubmitType : Table<Nothing>("submit_type") {
    val id = int("id").primaryKey()
    val text = varchar("text")
    val name = varchar("name")
    val createTime = datetime("create_time")
}