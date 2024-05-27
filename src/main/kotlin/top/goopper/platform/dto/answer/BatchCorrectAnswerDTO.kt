package top.goopper.platform.dto.answer

data class BatchCorrectAnswerDTO(
    val ids: List<Int>,
    val comment: String,
    val score: Int,
)
