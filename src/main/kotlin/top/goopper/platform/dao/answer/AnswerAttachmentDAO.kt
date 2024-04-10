package top.goopper.platform.dao.answer

import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.AttachmentDTO
import top.goopper.platform.table.answer.AnswerAttachment

@Repository
class AnswerAttachmentDAO(
    private val database: Database
) {
    fun batchCreateAnswerAttachment(attachments: List<AttachmentDTO>, answerId: Int) {
        database.batchInsert(AnswerAttachment) {
            attachments.forEach { attachment ->
                item {
                    set(it.answerId, answerId)
                    set(it.attachmentId, attachment.id)
                }
            }
        }
    }
}