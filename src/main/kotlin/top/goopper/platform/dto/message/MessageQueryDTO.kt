package top.goopper.platform.dto.message

data class MessageQueryDTO(
    val page: Int = 1,
    val title: String = "",
    // -1 for all types
    val typeId: Int = -1
)
