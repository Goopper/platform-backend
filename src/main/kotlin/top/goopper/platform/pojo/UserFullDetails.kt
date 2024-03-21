package top.goopper.platform.pojo

import top.goopper.platform.dto.UserDTO

data class UserFullDetails(
    val raw: UserDTO,
    val encodedPassword: String
)