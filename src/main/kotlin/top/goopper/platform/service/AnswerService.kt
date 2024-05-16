package top.goopper.platform.service

import org.springframework.dao.DuplicateKeyException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dao.AttachmentDAO
import top.goopper.platform.dao.answer.AnswerAttachmentDAO
import top.goopper.platform.dao.answer.AnswerDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.answer.AnswerDTO
import top.goopper.platform.dto.answer.CorrectAnswerDTO
import top.goopper.platform.dto.answer.SubmitAnswerDTO
import top.goopper.platform.dto.answer.SubmitInfoDTO
import top.goopper.platform.dto.message.MessageDTO
import top.goopper.platform.enum.MessageTypeEnum
import top.goopper.platform.enum.TaskSubmitTypeEnum
import top.goopper.platform.utils.MessageUtils
import top.goopper.platform.utils.RedisUtils
import java.time.LocalDateTime
import java.time.Year

@Service
class AnswerService(
    private val messageService: MessageService,
    private val answerDAO: AnswerDAO,
    private val answerAttachmentDAO: AnswerAttachmentDAO,
    private val attachmentDAO: AttachmentDAO,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val redisUtils: RedisUtils,
    private val messageUtils: MessageUtils
) {
    // TODO: optimize execution time
    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun submitTask(dto: SubmitAnswerDTO, uid: Int) {
        val info: SubmitInfoDTO = answerDAO.getSubmitInfo(dto, uid)
        when (info.submitTypeId) {
            TaskSubmitTypeEnum.NO_NEED.id -> {
                // create answer without send a message
                try {
                    answerDAO.createAnswer(dto, info)
                } catch (e: DuplicateKeyException) {
                    throw Exception("You have already submitted this task")
                }
            }
            TaskSubmitTypeEnum.COMMON_ONLY.id -> {
                val message = MessageDTO(
                    title = messageUtils.correctTitle,
                    content = messageUtils.buildAnswerCorrectRequestMessageContent(info.taskName),
                    typeId = MessageTypeEnum.REQUIRE_CORRECT.id,
                    senderId = uid,
                    receiverId = info.teacherId,
                )
                // send message to course teacher
                val messageId = messageService.send(message)
                // create answer
                try {
                    val answerId = answerDAO.createAnswer(dto, info, messageId)
                    val attachment = dto.attachments
                    if (attachment.isNotEmpty()) {
                        // add attachment
                        attachmentDAO.batchCreateAttachment(attachment)
                        // add attachment to answer
                        answerAttachmentDAO.batchCreateAnswerAttachment(attachment, answerId)
                    }
                } catch (e: DuplicateKeyException) {
                    throw Exception("You have already submitted this task")
                }
            }
            else -> {
                throw Exception("Unknown task submit type")
            }
        }

        // course finished task count +1 with mysql trigger `task_submit_trigger`
        redisTemplate.execute {
            // set latest learned info to redis
            val currentKey = redisUtils.buildLatestLearnedKey(uid, info.courseId)
            val fieldValues = mapOf(
                redisUtils.LATEST_LEARNED_SECTION to info.sectionName.toByteArray(),
                redisUtils.LATEST_LEARNED_TASK to info.taskName.toByteArray(),
                redisUtils.LATEST_LEARNED_DATE to LocalDateTime.now().toString().toByteArray()
            )
            it.hashCommands()
                .hMSet(currentKey, fieldValues)
            // set learned status to redis
            // get current year
            val currentYear = Year.now().value
            val currentDay = LocalDateTime.now().dayOfYear
            val yearKey = redisUtils.buildYearLearnedKey(uid, currentYear)
            val setKey = redisUtils.buildLearnedYearsListKey(uid)
            it.setCommands().sAdd(setKey, currentYear.toString().toByteArray())
            it.stringCommands().setBit(yearKey, currentDay.toLong(), true)
        }
    }

    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun correctTask(correctAnswerDTO: CorrectAnswerDTO) {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val studentId = answerDAO.correctAnswer(correctAnswerDTO)
        // send a message to student
        val message = MessageDTO(
            title = messageUtils.correctFinishedTitle,
            content = messageUtils.buildAnswerCorrectResultMessageContent(
                correctAnswerDTO.score,
                correctAnswerDTO.comment
            ),
            typeId = MessageTypeEnum.CORRECTED.id,
            senderId = user.id,
            receiverId = studentId,
        )
        messageService.send(message)
    }

    fun getSubmittedAnswer(messageId: Int): AnswerDTO {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val answer = try {
            answerDAO.getAnswerByMessageId(user.id, messageId)
        } catch (e: Exception) {
            throw Exception("No answer found")
        }
        val attachments = attachmentDAO.loadAnswerAttachments(answer.answerId)
        answer.attachments = attachments
        return answer
    }

}