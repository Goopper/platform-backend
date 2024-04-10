package top.goopper.platform.dao.answer

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dao.message.MessageDAO
import top.goopper.platform.dto.answer.AnswerDTO
import top.goopper.platform.dto.answer.CorrectAnswerDTO
import top.goopper.platform.dto.answer.SubmitAnswerDTO
import top.goopper.platform.table.Group
import top.goopper.platform.table.User
import top.goopper.platform.table.answer.Answer
import top.goopper.platform.table.message.UserMessage
import top.goopper.platform.table.task.Task
import java.time.LocalDateTime

/**
 * DAO for table answer.
 * Add will trigger the `task_submit_trigger` in the database.
 */
@Repository
class AnswerDAO(
    private val database: Database,
    private val messageDAO: MessageDAO
) {

    fun createAnswer(dto: SubmitAnswerDTO, uid: Int, messageId: Int?): Int {
        val id = database.insertAndGenerateKey(Answer) {
            set(it.courseId, dto.courseId)
            set(it.sectionId, dto.sectionId)
            set(it.taskId, dto.taskId)
            set(it.studentId, uid)
            set(it.content, dto.content)
            set(it.messageId, messageId)
        } as Int

        return id
    }

    /**
     * Correct the answer.
     * @return answer's student id
     * @throws IllegalArgumentException if no answer found
     */
    fun correctAnswer(correctAnswerDTO: CorrectAnswerDTO): Int {
        database.update(Answer) {
            set(it.comment, correctAnswerDTO.comment)
            set(it.score, correctAnswerDTO.score)
            set(it.corrected, true)
            set(it.modifyTime, LocalDateTime.now())
            where {
                it.id eq correctAnswerDTO.id
            }
        }
        val studentId = database.from(Answer)
            .select(Answer.studentId)
            .where {
                Answer.id eq correctAnswerDTO.id
            }
            .map { it[Answer.studentId]!! }
            .firstOrNull() ?: throw IllegalArgumentException("No answer found")
        return studentId
    }

    /**
     * Get the answer by the message id.
     * @param teacherId the teacher id
     * @param messageId the user_message id
     */
    fun getAnswerByMessageId(teacherId: Int, messageId: Int): AnswerDTO {
        val teacherAlias = User.aliased("teacher")

        val currentAnswerAlias = Answer.aliased("current")
        // This is user_message! NOT message!!!
        val currentUserMessageAlias = UserMessage.aliased("currentMessage")
        val currentStudentAlias = User.aliased("currentUser")
        val currentGroupAlias = Group.aliased("currentGroup")
        val currentTaskAlias = Task.aliased("currentTask")

        val nextAnswerAlias = Answer.aliased("next")
        // This is user_message! NOT message!!!
        val nextUserMessageAlias = UserMessage.aliased("nextMessage")
        val nextStudentAlias = User.aliased("nextUser")
        val nextGroupAlias = Group.aliased("nextGroup")
        val nextTaskAlias = Task.aliased("nextTask")

        val result = database.from(teacherAlias)
            // current message
            .innerJoin(currentUserMessageAlias, teacherAlias.id eq currentUserMessageAlias.receiverId)
            .innerJoin(currentAnswerAlias, currentUserMessageAlias.messageId eq currentAnswerAlias.messageId)
            .innerJoin(currentStudentAlias, currentAnswerAlias.studentId eq currentStudentAlias.id)
            .innerJoin(currentGroupAlias, currentStudentAlias.groupId eq currentGroupAlias.id)
            .innerJoin(currentTaskAlias, currentAnswerAlias.taskId eq currentTaskAlias.id)
            // next message
            .leftJoin(
                nextUserMessageAlias,
                (teacherAlias.id eq nextUserMessageAlias.receiverId) and
                        (nextUserMessageAlias.id greater currentUserMessageAlias.id)
            )
            .leftJoin(
                nextAnswerAlias,
                nextUserMessageAlias.messageId eq nextAnswerAlias.messageId and
                        (nextAnswerAlias.id greater currentAnswerAlias.id)
            )
            .leftJoin(nextStudentAlias, nextAnswerAlias.studentId eq nextStudentAlias.id)
            .leftJoin(nextGroupAlias, nextStudentAlias.groupId eq nextGroupAlias.id)
            .leftJoin(nextTaskAlias, nextAnswerAlias.taskId eq nextTaskAlias.id)
            .select(
                // current
                currentAnswerAlias.id,
                currentAnswerAlias.content,
                currentStudentAlias.name,
                currentGroupAlias.name,
                currentTaskAlias.name,
                // next
                nextUserMessageAlias.id,
                nextTaskAlias.name,
                nextGroupAlias.name,
                nextStudentAlias.name
            )
            .where {
                (teacherAlias.id eq teacherId) and
                        (currentAnswerAlias.messageId eq messageId) and
                        // score eq null, not be corrected
                        (nextAnswerAlias.corrected eq false)
            }
            .map {
                AnswerDTO(
                    it[currentAnswerAlias.id]!!,
                    it[currentStudentAlias.name]!!,
                    it[currentGroupAlias.name]!!,
                    it[currentTaskAlias.name]!!,
                    it[currentAnswerAlias.content]!!,
                    emptyList(),
                    it[nextTaskAlias.name],
                    it[nextStudentAlias.name],
                    it[nextGroupAlias.name],
                    it[nextUserMessageAlias.id]
                )
            }.firstOrNull() ?: throw IllegalArgumentException("No answer found")

        return result
    }

}