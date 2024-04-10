package top.goopper.platform.dto

data class UserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String,
    val sex: Boolean,
    val number: Int,
    val roleId: Int,
    val groupId: Int?,
    val roleName: String,
    val groupName: String?,
)