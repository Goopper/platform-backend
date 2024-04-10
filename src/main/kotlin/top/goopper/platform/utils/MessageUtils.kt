package top.goopper.platform.utils

import org.springframework.stereotype.Component

@Component
class MessageUtils {

    val correctTitle = "作业批改消息"

    val correctFinishedTitle = "作业批改完成"

    fun buildAnswerCorrectRequestMessageContent(taskName: String): String = """
        您有一份作业需要批改，请及时处理。
        作业名称：$taskName
    """.trimIndent()

    fun buildAnswerCorrectResultMessageContent(score: Int, comment: String): String = """
        您的成果已批改完毕，请查收批改结果。
        分数：$score
        
        评语：$comment
    """.trimIndent()

}