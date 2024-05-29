package top.goopper.platform.dto.answer

data class AnswerPageDTO(
    val list: List<AnswerDTO>,
    val page: Int,
    val totalPage: Int,
    val total: Int,
)
