package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.table.*

@Component
class AttachmentDAO(private val database: Database) {

    // create attachment and return the id
    fun createAttachment(attachmentDTO: AttachmentDTO): Long {
        val id = database.insertAndGenerateKey(Attachment) {
            set(it.filename, attachmentDTO.filename)
            set(it.originalFilename, attachmentDTO.originalFilename)
            set(it.url, attachmentDTO.url)
            set(it.size, attachmentDTO.size)
            set(it.type, attachmentDTO.type)
            set(it.contentMD5, attachmentDTO.md5)
        } as Long
        return id
    }

    // batch create attachment and return the ids
    fun batchCreateAttachment(attachments: List<AttachmentDTO>) {
        attachments.forEach {
            it.id = createAttachment(it)
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    fun deleteAttachmentByName(filename: String) {
        val count = database.delete(Attachment) {
            it.filename eq filename
        }
        if (count > 1) {
            throw Exception("Delete attachment failed")
        }
    }

    /**
     * Delete all attachments that are not used in any course
     * @return the filename of the deleted attachments
     */
    @Transactional(rollbackFor = [Exception::class])
    fun batchDeleteUnusedAttachment(): List<String>{
        val query = database.from(Attachment)
            .leftJoin(CourseAttachment, CourseAttachment.attachmentId eq Attachment.id)
            .leftJoin(TaskAttachment, TaskAttachment.attachmentId eq Attachment.id)
            .leftJoin(StudentTaskAttachment, StudentTaskAttachment.attachmentId eq Attachment.id)
            .select()
            .where { CourseAttachment.attachmentId.isNull() }
        database.delete(Attachment) {
            it.id inList query
        }
        return query.map { it[Attachment.filename]!! }
    }

    fun loadCourseAttachments(courseId: Long): List<AttachmentDTO> {
        val attachments = database.from(Attachment)
            .innerJoin(CourseAttachment, CourseAttachment.attachmentId eq Attachment.id)
            .select()
            .where(CourseAttachment.courseId eq courseId)
            .map {
                AttachmentDTO(
                    id = it[Attachment.id]!!,
                    filename = it[Attachment.filename]!!,
                    url = it[Attachment.url]!!,
                    type = it[Attachment.type]!!,
                    size = it[Attachment.size]!!,
                    originalFilename = it[Attachment.originalFilename]!!,
                    md5 = it[Attachment.contentMD5]!!
                )
            }
        return attachments
    }

    fun loadTaskAttachments(taskId: Long): List<AttachmentDTO> {
        val attachments = database.from(TaskAttachment)
            .innerJoin(Attachment, Attachment.id eq TaskAttachment.attachmentId)
            .select()
            .where(TaskAttachment.taskId eq taskId)
            .map {
                AttachmentDTO(
                    id = it[Attachment.id]!!,
                    filename = it[Attachment.filename]!!,
                    url = it[Attachment.url]!!,
                    type = it[Attachment.type]!!,
                    size = it[Attachment.size]!!,
                    originalFilename = it[Attachment.originalFilename]!!,
                    md5 = it[Attachment.contentMD5]!!
                )
            }
        return attachments
    }

}