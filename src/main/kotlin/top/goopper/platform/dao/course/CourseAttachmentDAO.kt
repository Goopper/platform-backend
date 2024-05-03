package top.goopper.platform.dao.course

import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.springframework.stereotype.Repository
import top.goopper.platform.table.course.CourseAttachment

@Repository
class CourseAttachmentDAO(
    private val database: Database
) {

    fun batchInsertCourseAttachment(courseId: Int, attachmentIds: List<Int>) {
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

    fun deleteCourseAttachment(courseId: Int, attachmentId: Int) {
        database.delete(CourseAttachment) {
            it.courseId eq courseId
            it.attachmentId eq attachmentId
        }
    }

}