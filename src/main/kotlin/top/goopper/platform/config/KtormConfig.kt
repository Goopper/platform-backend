package top.goopper.platform.config

import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class KtormConfig(private val dataSource: DataSource) {

    @Bean
    fun database() = Database.connectWithSpringSupport(dataSource)

//    @Bean
//    fun analyticalDB() = Database.connect(
//        driver = "com.mysql.cj.jdbc.Driver",
//        url = "jdbc:mysql://10.200.0.2:3306/baize_5.0",
//        user = "analyst",
//        password = "&A83TzzO@G@rQ622^*hD@t%bu\$ZpWvuE"
//    )

    @Bean
    fun ktormModule() = KtormModule()
}