package top.goopper.platform.dao

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.table.Attachment
import top.goopper.platform.table.answer.AnswerAttachment
import top.goopper.platform.table.course.CourseAttachment
import top.goopper.platform.table.task.TaskAttachment

@Repository
class AttachmentDAO(private val database: Database) {

    // create attachment and return the id
    fun createAttachment(dto: AttachmentDTO): Int {
        val id = database.insertAndGenerateKey(Attachment) {
            set(it.filename, dto.filename)
            set(it.originalFilename, dto.originalFilename)
            set(it.url, dto.url)
            set(it.size, dto.size)
            set(it.type, dto.type)
            set(it.contentMD5, dto.md5)
        } as Int
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
        val attachments = database.from(Attachment)
            .leftJoin(CourseAttachment, CourseAttachment.attachmentId eq Attachment.id)
            .leftJoin(TaskAttachment, TaskAttachment.attachmentId eq Attachment.id)
            .leftJoin(AnswerAttachment, AnswerAttachment.attachmentId eq Attachment.id)
            .select()
            .where { CourseAttachment.attachmentId.isNull() }
            .map { Pair(it[Attachment.id]!!, it[Attachment.filename]!!)}
        if (attachments.isEmpty()) {
            return emptyList()
        }
        database.delete(Attachment) {
            it.id inList attachments.map { a -> a.first }
        }
        // return filenames
        return attachments.map { it.second }
    }

    fun loadCourseAttachments(courseId: Int): List<AttachmentDTO> {
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

    fun loadTaskAttachments(taskId: Int): List<AttachmentDTO> {
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

    fun loadAnswerAttachments(id: Int): List<AttachmentDTO> {
        val attachments = database.from(AnswerAttachment)
            .innerJoin(Attachment, Attachment.id eq AnswerAttachment.attachmentId)
            .select()
            .where(AnswerAttachment.answerId eq id)
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