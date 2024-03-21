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

    @Bean
    fun ktormModule() = KtormModule()
}