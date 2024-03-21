package top.goopper.platform

import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import org.ktorm.dsl.forEach
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import top.goopper.platform.entity.User

@SpringBootTest
class PlatformApplicationTests {

    @Test
    fun contextLoads() {
    }

}
