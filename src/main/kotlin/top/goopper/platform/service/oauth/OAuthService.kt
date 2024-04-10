package top.goopper.platform.service.oauth

import eu.bitwalker.useragentutils.UserAgent
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.oauth.OAuthDAO
import top.goopper.platform.dao.oauth.ProviderDAO
import top.goopper.platform.dto.OAuthDTO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.JwtSubject
import top.goopper.platform.service.JwtTokenService

@Service
class OAuthService(
    private val oauthDAO: OAuthDAO,
    private val jwtTokenService: JwtTokenService,
    private val githubService: GithubService,
    private val providerDAO: ProviderDAO
) {

    /**
     * Authenticate user by OAuth
     * @return jwt token
     */
    fun authenticate(oauthId: String, providerName: String, userAgentStr: String): String {
        val user = oauthDAO.loadUserByOAuth(oauthId, providerName)
        val userAgent = UserAgent.parseUserAgentString(userAgentStr)
        // save to SecurityContext
        val authentication =
            UsernamePasswordAuthenticationToken(user, user.number, listOf(GrantedAuthority { user.roleName }))
        SecurityContextHolder.getContext().authentication = authentication
        // create jwt and return
        val jwt = jwtTokenService.storeAuthToken(
            JwtSubject(
                uid = user.id,
                number = user.number,
                name = user.name,
                roleName = user.roleName,
                browserName = userAgent.browser.name,
                deviceName = userAgent.operatingSystem.deviceType.name,
                ua = userAgentStr
            )
        )
        return jwt
    }

    /**
     * Bind user by OAuth
     * @return true if success
     */
    fun bindUserWithOAuth(oauthId: String, oauthName: String, providerName: String, isRebind: Boolean) {
        // load user form SecurityContext @see UserDTO
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        if (providerName == "github") {
            // throw exception if not exists
            githubService.checkExists(oauthName, oauthId)
        }
        try {
            oauthDAO.bindUserWithOAuth(user.id, oauthId, oauthName, providerName, isRebind)
        } catch (e: DuplicateKeyException) {
            throw Exception("OAuth binding already exists")
        }
    }

    // throw exception if bind failed
    fun unbindUserWithOAuth(providerName: String) {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        oauthDAO.unbindUserWithOAuth(user.id, providerName)
    }

    fun getOAuthBindingList(): List<OAuthDTO> {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        val binds = providerDAO.loadOAuthBindingList(user.id)
        return binds
    }

}