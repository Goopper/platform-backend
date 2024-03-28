package top.goopper.platform.dto

data class OAuthDTO(
    var id: Long,
    var name: String,
    var bind: Boolean,
    var bindUsername: String?,
    var bindId: String?,
)