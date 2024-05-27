package top.goopper.platform.dao.message

import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.message.MessageCreateDTO
import top.goopper.platform.dto.message.MessageTypeListDTO
import top.goopper.platform.table.message.Message
import top.goopper.platform.table.message.MessageType

@Repository
class MessageDAO(private val database: Database) {

    fun getTypes(): List<MessageTypeListDTO> {
        val types = database.from(MessageType)
            .select()
            .map {
                MessageTypeListDTO(
                    id = it[MessageType.id]!!,
                    name = it[MessageType.name]!!,
                    color = it[MessageType.color]!!
                )
            }

        return types
    }

    fun createMessage(messageDTO: MessageCreateDTO): Int {
        val id = database.insertAndGenerateKey(Message) {
            set(it.title, messageDTO.title)
            set(it.content, messageDTO.content)
            set(it.typeId, messageDTO.typeId)
        } as Int
        return id
    }

}