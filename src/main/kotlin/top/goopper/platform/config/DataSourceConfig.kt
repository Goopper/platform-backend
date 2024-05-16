package top.goopper.platform.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.basic")
    fun basicDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun basicDataSource(): DataSource {
        return basicDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }

    @Bean("basicTransactionManager")
    fun basicTransactionManager(
        @Qualifier("basicDataSource") dataSource: DataSource
    ): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    @ConfigurationProperties("spring.datasource.analytical")
    fun analyticalDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun analyticalDataSource(): DataSource {
        return analyticalDataSourceProperties()
            .initializeDataSourceBuilder()
            .build()
    }

    @Bean("analyticalTransactionManager")
    fun analyticalTransactionManager(
        @Qualifier("analyticalDataSource") dataSource: DataSource
    ): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

}