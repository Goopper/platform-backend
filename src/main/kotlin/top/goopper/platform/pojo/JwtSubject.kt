package top.goopper.platform.pojo

data class JwtSubject(
    val uid: Int,
    val number: Int,
    val name: String,
    val roleName: String,
    val browserName: String,
    val deviceName: String,
    val ua: String
)