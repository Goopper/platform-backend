package top.goopper.platform.config

import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class KtormConfig() {

    @Bean
    fun database(@Qualifier("basicDataSource") basicDataSource: DataSource): Database =
        Database.connectWithSpringSupport(basicDataSource)

    @Bean
    fun analyticalDB(@Qualifier("analyticalDataSource") analyticalDataSource: DataSource): Database? =
        Database.connectWithSpringSupport(analyticalDataSource)

    @Bean
    fun ktormModule() = KtormModule()
}