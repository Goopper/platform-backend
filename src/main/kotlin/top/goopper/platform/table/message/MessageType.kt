package top.goopper.platform.table.message

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object MessageType : Table<Nothing>("message_type") {

    val id = int("id").primaryKey()
    val name = varchar("name")
    val color = varchar("color")
    val createTime = datetime("create_time")

}