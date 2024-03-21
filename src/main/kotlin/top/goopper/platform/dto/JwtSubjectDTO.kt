package top.goopper.platform.dto

data class JwtSubjectDTO(
    val uid: Long,
    val number: Long,
    val name: String,
    val roleName: String,
    val browserName: String,
    val deviceName: String,
    val ua: String
)