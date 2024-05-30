package top.goopper.platform.dao.answer

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.answer.*
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

    fun getAnswerById(answerId: Int): AnswerDetailDTO {
        val answer = database.from(Answer)
            .innerJoin(Task, Task.id eq Answer.taskId)
            .innerJoin(Section, Section.id eq Answer.sectionId)
            .innerJoin(Course, Course.id eq Section.courseId)
            .innerJoin(User, User.id eq Answer.studentId)
            .innerJoin(Group, Group.id eq User.groupId)
            .select(
                Answer.id, Answer.content, Task.content, User.number, User.name, Group.name, Course.name, Section.name,
                Task.name, Answer.corrected, Answer.createTime
            )
            .where { (Answer.id eq answerId) and (Answer.messageId.isNotNull()) }
            .map {
                AnswerDetailDTO(
                    id = it[Answer.id]!!,
                    answerContent = it[Answer.content]!!,
                    taskContent = it[Task.content]!!,
                    attachments = emptyList(),
                    answer = buildAnswerDTO(it)
                )
            }
            .firstOrNull() ?: throw IllegalArgumentException("No answer found")
        return answer
    }

    fun getSubmittedAnswers(answerQueryDTO: AnswerQueryDTO): AnswerPageDTO {
        var condition = (UserMessage.receiverId eq answerQueryDTO.teacherId)
            .and(Section.name like "%${answerQueryDTO.sectionName}%")
            .and(Task.name like "%${answerQueryDTO.taskName}%")
            .and(User.name like "%${answerQueryDTO.studentName}%")
        if (answerQueryDTO.groupId != null) {
            condition = condition.and(User.groupId eq answerQueryDTO.groupId)
        }
        if (answerQueryDTO.courseId != null) {
            condition = condition.and(Course.id eq answerQueryDTO.courseId)
        }
        if (answerQueryDTO.corrected != null) {
            condition = condition.and(Answer.corrected eq answerQueryDTO.corrected)
        }

        val query = database.from(UserMessage)
            .innerJoin(Answer, Answer.messageId eq UserMessage.id)
            .innerJoin(Task, Task.id eq Answer.taskId)
            .innerJoin(Section, Section.id eq Answer.sectionId)
            .innerJoin(Course, Course.id eq Section.courseId)
            .innerJoin(User, User.id eq Answer.studentId)
            .innerJoin(Group, Group.id eq User.groupId)
            .select(
                Answer.id, User.number, User.name, Group.name, Course.name, Section.name,
                Task.name, Answer.corrected, Answer.createTime
            )
            .where { condition }
            .orderBy(Answer.createTime.desc())
            .limit((answerQueryDTO.page - 1) * answerQueryDTO.pageSize, answerQueryDTO.pageSize)
        val answers = query.map {
            buildAnswerDTO(it)
        }
        val total = query.totalRecordsInAllPages
        var totalPage = total / answerQueryDTO.pageSize + if (total % answerQueryDTO.pageSize == 0) 0 else 1
        if (total == 0) {
            totalPage = 1
        }
        return AnswerPageDTO(
            page = answerQueryDTO.page,
            total = total,
            list = answers,
            totalPage = totalPage
        )
    }

    fun getAnswerIdsAndTaskNames(answerIds: List<Int>): List<AnswerIdWithTaskNameDTO> {
        val result = database.from(Answer)
            .innerJoin(Task, Task.id eq Answer.taskId)
            .select(Answer.id, Task.name, Answer.corrected)
            .where { Answer.id inList answerIds }
            .map {
                AnswerIdWithTaskNameDTO(
                    answerId = it[Answer.id]!!,
                    taskName = it[Task.name]!!,
                    corrected = it[Answer.corrected]!!
                )
            }
        return result
    }

    private fun buildAnswerDTO(it: QueryRowSet) = AnswerDTO(
        id = it[Answer.id]!!,
        number = it[User.number]!!,
        studentName = it[User.name]!!,
        groupName = it[Group.name]!!,
        courseName = it[Course.name]!!,
        sectionName = it[Section.name]!!,
        taskName = it[Task.name]!!,
        corrected = it[Answer.corrected]!!,
        submitTime = it[Answer.createTime]!!
    )

    fun correctTasks(batchCorrectAnswerDTO: BatchCorrectAnswerDTO, teacherId: Int): List<Int> {
        database.update(Answer) {
            set(it.comment, batchCorrectAnswerDTO.comment)
            set(it.score, batchCorrectAnswerDTO.score)
            set(it.corrected, true)
            set(it.modifyTime, LocalDateTime.now())
            where {
                (it.id inList batchCorrectAnswerDTO.ids) and (it.corrected eq false)
            }
        }

        // corrected student ids
        val studentIds = database.from(Answer)
            .select(Answer.studentId)
            .where {
                (Answer.id inList batchCorrectAnswerDTO.ids) and (Answer.corrected eq true)
            }
            .map { it[Answer.studentId]!! }
        return studentIds
    }

    fun getCorrectedAnswer(id: Int): CorrectedAnswerDetailDTO {
        val answer = database.from(Answer)
            .innerJoin(Task, Task.id eq Answer.taskId)
            .innerJoin(Section, Section.id eq Answer.sectionId)
            .innerJoin(Course, Course.id eq Section.courseId)
            .innerJoin(User, User.id eq Answer.studentId)
            .innerJoin(Group, Group.id eq User.groupId)
            .select(
                Answer.id, Answer.content, Task.content, User.number, User.name, Group.name, Course.name, Section.name,
                Task.name, Answer.corrected, Answer.createTime, Answer.score, Answer.comment
            )
            .where { (Answer.id eq id) and (Answer.corrected eq true) }
            .map {
                CorrectedAnswerDetailDTO(
                    id = it[Answer.id]!!,
                    answerContent = it[Answer.content]!!,
                    taskContent = it[Task.content]!!,
                    attachments = emptyList(),
                    answer = buildAnswerDTO(it),
                    score = it[Answer.score]!!,
                    comment = it[Answer.comment]!!
                )
            }
            .firstOrNull() ?: throw IllegalArgumentException("No answer found")
        return answer
    }

}