package top.goopper.platform.dto

data class AttachmentDTO(
    var id: Int?,
    val filename: String,
    val originalFilename: String?,
    val url: String,
    val size: Long,
    val type: String,
    val md5: String
)