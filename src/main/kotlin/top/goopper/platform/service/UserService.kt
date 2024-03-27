package top.goopper.platform.service

import eu.bitwalker.useragentutils.UserAgent
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import top.goopper.platform.dao.UserDAO
import top.goopper.platform.dto.JwtSubjectDTO

@Service
class UserService(
    private val jwtTokenService: JwtTokenService,
    private val userDAO: UserDAO,
    private val passwordEncoder: BCryptPasswordEncoder
) : UserDetailsService {

    /**
     * Authenticate user with number and password
     * @param number user number
     * @param password encoded password
     * @param userAgentStr user agent
     * @return jwt token
     * @throws Exception if user does not exist or password is incorrect
     */
    fun authenticate(number: Long, password: String, userAgentStr: String): String {
        // load user with number
        val fullUserDetails = userDAO.loadFullUserByUserNumber(number)
        val user = fullUserDetails.raw
        // validate password
        if (!passwordEncoder.matches(password, fullUserDetails.encodedPassword)) {
            throw Exception("Password incorrect")
        }
        val userAgent = UserAgent.parseUserAgentString(userAgentStr)
        // save to SecurityContext
        val authentication = UsernamePasswordAuthenticationToken(
            user,
            user.id,
            listOf(GrantedAuthority { user.roleName })
        )
        SecurityContextHolder.getContext().authentication = authentication
        // create payload
        val payloadDTO = JwtSubjectDTO(
            user.id, user.number, user.name, user.roleName,
            userAgent.browser.name, userAgent.operatingSystem.name,
            userAgentStr
        )
        // store jwt and return
        val jwt = jwtTokenService.storeAuthToken(
            payloadDTO
        )
        return jwt
    }

    /**
     * Implement loadUserByUsername method for Spring Security
     * @param username user number
     * @return UserDetails
     * @throws Exception if user does not exist
     */
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userDAO.loadUserByNumber(username?.toLong() ?: 0)
        return User(user.number.toString(), user.number.toString(), listOf(
            GrantedAuthority { user.roleName }
        ))
    }

    /**
     * Logout user
     * @param token jwt token
     * @throws Exception if token is invalid
     */
    fun logout(token: String) {
        jwtTokenService.removeAuthToken(token)
    }

}