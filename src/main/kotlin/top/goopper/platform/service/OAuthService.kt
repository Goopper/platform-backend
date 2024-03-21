package top.goopper.platform.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.UserDAO
import top.goopper.platform.dto.UserDTO

@Service
class OAuthService(private val userDAO: UserDAO) {

    fun bindUserWithOAuth(oauthId: String, oauthName: String, providerName: String): Boolean {
        // load user form SecurityContext @see UserDTO
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val result = userDAO.bindUserWithOAuth(user.id, oauthId, oauthName, providerName)
        return result
    }

}