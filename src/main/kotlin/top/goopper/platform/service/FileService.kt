package top.goopper.platform.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.PutObjectRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.dto.AttachmentDTO
import java.io.File
import java.util.UUID

@Service
class FileService(
    private val s3Client: AmazonS3
) {

    @Value("\${s3.endpoint}")
    lateinit var endpoint: String

    @Value("\${s3.bucket}")
    lateinit var bucket: String

    private val logger = LoggerFactory.getLogger(FileService::class.java)

    // upload file to s3
    fun upload(upload: MultipartFile): AttachmentDTO {
        // convert MultipartFile to File
        val file = File.createTempFile("temp", null)
        file.outputStream().use { it.write(upload.bytes) }
        val type = upload.originalFilename?.substringAfterLast(".") ?: "unknown"
        val filename = UUID.randomUUID().toString().replace("-", "") + ".$type"
        // upload file to s3
        val request = PutObjectRequest(bucket, filename, file)
        request.cannedAcl = CannedAccessControlList.PublicRead
        val result = s3Client.putObject(request)
        // delete temp file
        file.delete()
        logger.info("File upload finish, md5: ${result.contentMd5}, name: $filename")
        return AttachmentDTO(
            id = null,
            filename = filename,
            originalFilename = upload.originalFilename,
            url = "$endpoint/$bucket/$filename",
            size = upload.size,
            type = type,
            md5 = result.contentMd5
        )
    }

    // delete file form s3
    fun delete(filename: String) {
        s3Client.deleteObject(bucket, filename)
        logger.info("File delete, filename: $filename")
    }

}