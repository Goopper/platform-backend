package top.goopper.platform.dao.message

import org.ktorm.database.Database
import org.ktorm.dsl.insert
import org.springframework.stereotype.Repository
import top.goopper.platform.table.message.MessageAnswer

@Repository
class MessageAnswerDAO(private val database: Database) {

    fun createMessageAnswer(messageId: Int, answerId: Int) {
        database.insert(MessageAnswer) {
            set(it.messageId, messageId)
            set(it.answerId, answerId)
        }
    }

}