package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dao.message.MessageDAO
import top.goopper.platform.dao.message.UserMessageDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.message.*

@Service
class MessageService(
    private val messageDAO: MessageDAO,
    private val userMessageDAO: UserMessageDAO,
) {

    private val pageSize = 10

    // get messages of a user
    fun getPage(dto: MessageQueryDTO): MessagePageDTO {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val result = userMessageDAO.getMessages(user.id, dto, pageSize)
        return result
    }

    // send messages to a user
    @Transactional(rollbackFor = [Exception::class], transactionManager = "basicTransactionManager")
    fun send(message: MessageDTO): Int {
        val messageCreateDTO = MessageCreateDTO(
            title = message.title,
            content = message.content,
            typeId = message.typeId,
        )
        val messageId = messageDAO.createMessage(messageCreateDTO)
        val dto = UserMessageDTO(
            senderId = message.senderId,
            receiverId = message.receiverId,
            messageId = messageId,
        )
        val userMessageId = userMessageDAO.createUserMessage(dto)
        return userMessageId
    }

    // receive one message
    fun receiveOne(id: Int) {
        userMessageDAO.readOne(id)
    }

    fun getTypes(): List<MessageTypeListDTO> {
        val types = messageDAO.getTypes()
        return types
    }

    // batch send messages
    @Transactional(rollbackFor = [Exception::class])
    fun batchSend(message: MessageBatchSendDTO) {
        val messageCreateDTO = MessageCreateDTO(
            title = message.title,
            content = message.content,
            typeId = message.typeId,
        )
        val messageId = messageDAO.createMessage(messageCreateDTO)
        val dtoList = message.receiverIds.map {
            UserMessageDTO(
                senderId = message.senderId,
                receiverId = it,
                messageId = messageId,
            )
        }
        userMessageDAO.batchCreateUserMessage(dtoList)
    }

}