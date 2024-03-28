package top.goopper.platform.dto

data class AttachmentDTO(
    val id: Long?,
    val filename: String,
    val originalFilename: String?,
    val url: String,
    val size: Long,
    val type: String,
    val md5: String
)