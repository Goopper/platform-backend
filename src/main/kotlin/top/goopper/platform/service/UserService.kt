package top.goopper.platform.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.bitwalker.useragentutils.UserAgent
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.dao.UserDAO
import top.goopper.platform.dto.DeviceDTO
import top.goopper.platform.dto.UserDTO
import top.goopper.platform.pojo.JwtSubject
import java.nio.charset.Charset

@Service
class UserService(
    private val jwtTokenService: JwtTokenService,
    private val userDAO: UserDAO,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val redisTemplate: RedisTemplate<String, String>,
    private val attachmentService: AttachmentService,
) : UserDetailsService {

    /**
     * Authenticate user with number and password
     * @param number user number
     * @param password encoded password
     * @param userAgentStr user agent
     * @return jwt token
     * @throws Exception if user does not exist or password is incorrect
     */
    fun authenticate(number: Int, password: String, userAgentStr: String): String {
        // load user with number
        val fullUserDetails = userDAO.loadFullUserByUserNumber(number)
        val user = fullUserDetails.raw
        // validate password
        if (!passwordEncoder.matches(password, fullUserDetails.encodedPassword)) {
            throw Exception("Password incorrect")
        }
        val userAgent = UserAgent.parseUserAgentString(userAgentStr)
        // save to SecurityContext
        updateCurrentAuthenticatedUser(user)
        // create payload
        val payloadDTO = JwtSubject(
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
        val user = userDAO.loadUserByNumber(username?.toInt() ?: 0)
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
        SecurityContextHolder.clearContext()
    }

    /**
     * Logout user with tokenID
     * @param tokenId jwt token id
     * @throws Exception if token is invalid
     */
    fun logout(tokenId: Int) {
        jwtTokenService.removeAuthToken(tokenId)
    }

    /**
     * Load user by id
     * @param id user id
     * @return user
     * @throws Exception if user does not exist
     */
    fun loadUserById(id: Int): UserDTO {
        val user = userDAO.loadUserById(id)
        return user
    }

    /**
     * Load devices by user id
     * @param uid user id
     * @return list of devices
     */
    fun loadDevice(uid: Int): List<DeviceDTO> {
        val pattern = "auth:$uid:*"
        val devices = mutableListOf<DeviceDTO>()
        redisTemplate.execute {
            val keys = it.keyCommands().keys(pattern.toByteArray()).orEmpty().toTypedArray()
            it.stringCommands().mGet(*keys).orEmpty().forEach { value ->
                val jwtToken = value.toString(Charset.forName("UTF-8"))
                val payload = jwtTokenService.getPayload(jwtToken)
                val subject = jacksonObjectMapper().readValue(payload.subject, JwtSubject::class.java)
                devices.add(DeviceDTO(
                    subject.hashCode().toString(),
                    subject.ua,
                    subject.deviceName,
                    payload.issuedAt.toString()
                ))
            }
        }
        return devices
    }

    /**
     * Update user email
     * TODO: Verify email and send email
     */
    fun updateEmail(old: String, new: String) {
        val user = currentAuthenticatedUser()
        if (user.email != old) {
            throw Exception("Old email incorrect")
        }
        userDAO.updateEmail(user.id, new)
        // update email in SecurityContext
        updateCurrentAuthenticatedUser(user.copy(email = new))
    }

    /**
     * Update user password
     */
    fun updatePassword(old: String, new: String) {
        val user = currentAuthenticatedUser()
        val encodedPassword = userDAO.loadFullUserByUserNumber(user.number).encodedPassword
        if (!passwordEncoder.matches(old, encodedPassword)) {
            throw Exception("Old password incorrect")
        }
        userDAO.updatePassword(user.id, passwordEncoder.encode(new))
        // revoke all tokens
        jwtTokenService.revokeAllTokensByUserId(user.id)
    }

    /**
     * Get current authenticated user
     */
    private fun currentAuthenticatedUser(): UserDTO {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        return user
    }

    /**
     * Update current authenticated user in SecurityContext
     */
    private fun updateCurrentAuthenticatedUser(user: UserDTO) {
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            user,
            user.number,
            listOf(GrantedAuthority { user.roleName })
        )
    }

    // upload avatar file to s3
    fun uploadAvatar(avatar: MultipartFile): String {
        val url = attachmentService.upload(avatar)
        return url
    }

    fun updateAvatar(url: String) {
        val user = SecurityContextHolder.getContext().authentication.principal as UserDTO
        userDAO.updateAvatar(user.id, url);
    }

    fun updateStudentPassword(password: String, uid: Int) {
        if (password.isEmpty()) {
            throw Exception("Password cannot be empty")
        }
        userDAO.updatePassword(uid, passwordEncoder.encode(password))
    }
}