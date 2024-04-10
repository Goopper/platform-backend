package top.goopper.platform.dao.message

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.message.MessageListDTO
import top.goopper.platform.dto.message.MessageQueryDTO
import top.goopper.platform.dto.message.UserMessageDTO
import top.goopper.platform.table.User
import top.goopper.platform.table.message.Message
import top.goopper.platform.table.message.UserMessage
import java.time.LocalDateTime

@Repository
class UserMessageDAO(private val database: Database) {

    fun createUserMessage(userMessageDTO: UserMessageDTO): Int {
        val id = database.insertAndGenerateKey(UserMessage) {
            set(it.senderId, userMessageDTO.senderId)
            set(it.receiverId, userMessageDTO.receiverId)
            set(it.messageId, userMessageDTO.messageId)
            set(it.readTime, userMessageDTO.readTime)
        } as Int

        return id
    }

    fun getMessages(uid: Int, dto: MessageQueryDTO, size: Int): List<MessageListDTO> {
        val messages = database.from(UserMessage)
            .leftJoin(User, User.id eq UserMessage.senderId)
            .innerJoin(Message, Message.id eq UserMessage.messageId)
            .select()
            .where {
                (UserMessage.receiverId eq uid) and
                        (Message.title like "%${dto.title}%") and
                        ((dto.typeId == -1) or (Message.typeId eq dto.typeId))
            }
            .limit((dto.page - 1) * size, size)
            .orderBy(UserMessage.createTime.desc())
            .map {
                MessageListDTO(
                    id = it[UserMessage.id]!!,
                    title = it[Message.title]!!,
                    content = it[Message.content]!!,
                    typeId = it[Message.typeId]!!,
                    sender = UserDTO(
                        id = it[UserMessage.senderId]!!,
                        name = it[User.name]!!,
                        avatar = it[User.avatar]!!,
                        email = it[User.email]!!,
                        sex = it[User.sex]!!,
                        number = it[User.number]!!,
                        roleId = -1,
                        roleName = "",
                        groupId = -1,
                        groupName = ""
                    ),
                    date = it[UserMessage.createTime]!!,
                    isRead = it[UserMessage.readTime] != null
                )
            }
        return messages
    }

    fun readOne(id: Int) {
        database.update(UserMessage) {
            set(it.readTime, LocalDateTime.now())
            where {
                it.id eq id
            }
        }
    }

}