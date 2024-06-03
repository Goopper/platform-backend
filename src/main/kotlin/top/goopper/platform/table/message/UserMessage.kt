package top.goopper.platform.table.message

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int

open class UserMessage(alias: String?) : Table<Nothing>("user_message", alias) {
    companion object : UserMessage(null)

    val id = int("id").primaryKey()
    val senderId = int("sender_id")
    val receiverId = int("receiver_id")
    // message content id
    val messageId = int("message_id")
    val readTime = datetime("read_time")
    val createTime = datetime("create_time")
    val answerId = int("answer_id")

    override fun aliased(alias: String) = UserMessage(alias)
}