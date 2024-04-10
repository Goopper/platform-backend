package top.goopper.platform.table.answer

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

object AnswerAttachment : Table<Nothing>("answer_attachment") {
    val id = int("id").primaryKey()
    val answerId = int("answer_id")
    val attachmentId = int("attachment_id")
    val createTime = datetime("create_time")
}