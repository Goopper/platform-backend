package top.goopper.platform.pojo

data class JwtSubject(
    val uid: Int,
    val number: Int,
    val deviceName: String,
    val ua: String,
    val forwardIps: String
)