package top.goopper.platform.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * S3 configuration, for file storage
 */
@Configuration
class S3Config {

    @Value("\${s3.access-key}")
    lateinit var accessKey: String

    @Value("\${s3.secret-key}")
    lateinit var secretKey: String

    @Value("\${s3.bucket}")
    lateinit var bucket: String

    @Value("\${s3.region}")
    lateinit var region: String

    @Value("\${s3.endpoint}")
    lateinit var endpoint: String

    // create s3 client bean
    @Bean
    fun s3Client(): AmazonS3 {
        val credentials = BasicAWSCredentials(accessKey, secretKey)
        val client = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(endpoint, region))
            .build()
        if (!client.doesBucketExistV2(bucket)) {
            client.createBucket(bucket)
        }
        return client
    }

}