package top.goopper.platform.table.answer

import org.ktorm.schema.*

open class Answer(alias: String?) : Table<Nothing>("answer", alias) {
    companion object : Answer(null)

    val id = int("id").primaryKey()
    val studentId = int("student_id")
    val courseId = int("course_id")
    val sectionId = int("section_id")
    val taskId = int("task_id")
    val score = int("score")
    val content = varchar("content")
    val comment = varchar("comment")
    val messageId = int("message_id")
    val corrected = boolean("corrected")
    val createTime = datetime("create_time")
    val modifyTime = datetime("modify_time")

    override fun aliased(alias: String) = Answer(alias)
}