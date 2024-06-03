package top.goopper.platform.table.message

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

/**
 * Message answer table.
 */
object MessageAnswer : Table<Nothing>("message_answer") {

    val id = int("id").primaryKey()
    val messageId = int("message_id")
    val answerId = int("answer_id")
    val createTime = datetime("create_time")

}