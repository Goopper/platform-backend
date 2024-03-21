package top.goopper.platform.dto

data class UserDTO(
    val id: Long,
    val name: String,
    val email: String,
    val avatar: String,
    val number: Long,
    val roleId: Long,
    val groupId: Long,
    val roleName: String,
    val groupName: String,
)