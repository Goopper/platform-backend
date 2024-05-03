package top.goopper.platform.dao.answer

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.answer.AnswerDTO
import top.goopper.platform.dto.answer.CorrectAnswerDTO
import top.goopper.platform.dto.answer.SubmitAnswerDTO
import top.goopper.platform.dto.answer.SubmitInfoDTO
import top.goopper.platform.table.Group
import top.goopper.platform.table.Section
import top.goopper.platform.table.User
import top.goopper.platform.table.answer.Answer
import top.goopper.platform.table.course.Course
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
) {

    fun createAnswer(dto: SubmitAnswerDTO, info: SubmitInfoDTO, messageId: Int? = null): Int {
        val id = database.insertAndGenerateKey(Answer) {
            set(it.courseId, info.courseId)
            set(it.sectionId, info.sectionId)
            set(it.taskId, dto.taskId)
            set(it.studentId, info.studentId)
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
     *
     * @exception Exception if no answer found
     */
    fun getAnswerByMessageId(teacherId: Int, messageId: Int): AnswerDTO {
        val query = database.from(UserMessage)
            .innerJoin(Answer, Answer.messageId eq UserMessage.id)
            .innerJoin(User, User.id eq UserMessage.senderId)
            .innerJoin(Group, Group.id eq User.groupId)
            .innerJoin(Task, Task.id eq Answer.taskId)
            .select(Answer.id, UserMessage.id, Answer.content, User.name, Group.name, Task.name)
            .where {
                (UserMessage.receiverId eq teacherId) and
                        (Answer.corrected eq false) and
                        (UserMessage.id gte messageId)
            }
            .limit(2)
        val iterator = query.iterator()
        val current = iterator.next()
        val result = AnswerDTO(
            answerId = current[Answer.id]!!,
            studentName = current[User.name]!!,
            groupName = current[Group.name]!!,
            taskName = current[Task.name]!!,
            content = current[Answer.content]!!,
            attachments = emptyList()
        )
        // has next answer need to correct
        if (iterator.hasNext()) {
            val next = iterator.next()
            result.apply {
                nextAnswerUserMessageId = next[UserMessage.id]
                nextAnswerTaskName = next[Task.name]
                nextAnswerStudentName = next[User.name]
                nextAnswerGroupName = next[Group.name]
            }
        }

        return result
    }

    fun getSubmitInfo(dto: SubmitAnswerDTO, uid: Int): SubmitInfoDTO {
        val result = database.from(Task)
            .innerJoin(Section, Section.id eq Task.sectionId)
            .innerJoin(Course, Course.id eq Section.courseId)
            .innerJoin(User, User.id eq Course.teacherId)
            .select(
                User.id, Course.id, Section.name,
                Task.submitTypeId, Task.name, Task.sectionId
            )
            .where { Task.id eq dto.taskId }
            .map {
                SubmitInfoDTO(
                    studentId = uid,
                    teacherId = it[User.id]!!,
                    courseId = it[Course.id]!!,
                    submitTypeId = it[Task.submitTypeId]!!,
                    taskName = it[Task.name]!!,
                    sectionName = it[Section.name]!!,
                    sectionId = it[Task.sectionId]!!
                )
            }.firstOrNull() ?: throw IllegalArgumentException("No task found")
        return result
    }

}