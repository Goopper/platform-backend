package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import top.goopper.platform.dao.message.MessageDAO
import top.goopper.platform.dao.message.UserMessageDAO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.message.MessageDTO
import top.goopper.platform.dto.message.MessageListDTO
import top.goopper.platform.dto.message.MessageQueryDTO
import top.goopper.platform.dto.message.UserMessageDTO

@Service
class MessageService(
    private val messageDAO: MessageDAO,
    private val userMessageDAO: UserMessageDAO,
) {

    private val pageSize = 10

    // get messages of a user
    fun getPage(dto: MessageQueryDTO): List<MessageListDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val messages = userMessageDAO.getMessages(user.id, dto, pageSize)
        return messages
    }

    // send messages to a user
    @Transactional(rollbackFor = [Exception::class])
    fun send(message: MessageDTO): Int {
        val messageId = messageDAO.createMessage(message)
        val dto = UserMessageDTO(
            senderId = message.senderId,
            receiverId = message.receiverId,
            messageId = messageId,
        )
        val userMessageId = userMessageDAO.createUserMessage(dto)
        return userMessageId
    }

    // send messages to all users
    fun sendToAll(message: MessageDTO) {

    }

    // receive one message
    fun receiveOne(id: Int) {
        userMessageDAO.readOne(id)
    }

}