package top.goopper.platform.dao.message

import org.ktorm.database.Database
import org.ktorm.dsl.insertAndGenerateKey
import org.springframework.stereotype.Repository
import top.goopper.platform.dto.message.MessageDTO
import top.goopper.platform.table.message.Message

@Repository
class MessageDAO(private val database: Database) {

    fun createMessage(messageDTO: MessageDTO): Int {
        val id = database.insertAndGenerateKey(Message) {
            set(it.title, messageDTO.title)
            set(it.content, messageDTO.content)
            set(it.typeId, messageDTO.typeId)
        } as Int
        return id
    }

}