package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.springframework.stereotype.Component
import top.goopper.platform.table.CourseAttachment

@Component
class CourseAttachmentDAO(
    private val database: Database
) {

    fun batchInsertCourseAttachment(courseId: Long, attachmentIds: List<Long>) {
        if (attachmentIds.isEmpty()) return
        database.batchInsert(CourseAttachment) {
            attachmentIds.forEach { attachmentId ->
                item {
                    set(it.courseId, courseId)
                    set(it.attachmentId, attachmentId)
                }
            }
        }
    }

}