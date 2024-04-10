package top.goopper.platform.table.message

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.varchar

/**
 * Message content table.
 */
object Message : Table<Nothing>("message") {

    val id = int("id").primaryKey()
    val title = varchar("title")
    val content = varchar("content")
    val typeId = int("type_id")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

}