package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.GroupDAO
import top.goopper.platform.dto.GroupDTO
import top.goopper.platform.dto.UserDTO

@Service
class GroupService(
    private val groupDAO: GroupDAO,
) {

    fun getByTeacherId(): List<GroupDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val groups = groupDAO.getByTeacherId(user.id)
        return groups
    }

}