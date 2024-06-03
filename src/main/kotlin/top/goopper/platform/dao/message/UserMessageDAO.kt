package top.goopper.platform.dao.message

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.dto.message.MessageListDTO
import top.goopper.platform.dto.message.MessagePageDTO
import top.goopper.platform.dto.message.MessageQueryDTO
import top.goopper.platform.dto.message.UserMessageDTO
import top.goopper.platform.table.User
import top.goopper.platform.table.message.Message
import top.goopper.platform.table.message.MessageAnswer
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

    fun getMessages(uid: Int, dto: MessageQueryDTO, size: Int): MessagePageDTO {
        val query = database.from(UserMessage)
            .leftJoin(User, User.id eq UserMessage.senderId)
            .innerJoin(Message, Message.id eq UserMessage.messageId)
            .leftJoin(MessageAnswer, MessageAnswer.messageId eq UserMessage.messageId)
            .select(
                UserMessage.id, Message.title, Message.content, Message.typeId, UserMessage.senderId, User.name,
                User.avatar, User.email, User.sex, User.number, UserMessage.createTime, UserMessage.readTime,
                MessageAnswer.answerId
            )
            .where {
                (UserMessage.receiverId eq uid) and
                        (Message.title like "%${dto.title}%") and
                        ((dto.typeId == -1) or (Message.typeId eq dto.typeId))
            }
            .limit((dto.page - 1) * size, size)
            .orderBy(UserMessage.createTime.desc())
        val messages = query.map {
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
                isRead = it[UserMessage.readTime] != null,
                answerId = it[MessageAnswer.answerId]
            )
        }
        val total = query.totalRecordsInAllPages
        val totalPage = total / size + if (total % size == 0) 0 else 1
        return MessagePageDTO(
            page = dto.page,
            total = total,
            totalPage = totalPage,
            list = messages
        )
    }

    fun readOne(id: Int) {
        database.update(UserMessage) {
            set(it.readTime, LocalDateTime.now())
            where {
                it.id eq id
            }
        }
    }

    fun batchCreateUserMessage(dtoList: List<UserMessageDTO>) {
        database.batchInsert(UserMessage) {
            for (dto in dtoList) {
                item {
                    set(it.senderId, dto.senderId)
                    set(it.receiverId, dto.receiverId)
                    set(it.messageId, dto.messageId)
                }
            }
        }
    }

}