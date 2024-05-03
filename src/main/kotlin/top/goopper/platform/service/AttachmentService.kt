package top.goopper.platform.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.dao.AttachmentDAO
import top.goopper.platform.dto.AttachmentDTO
import java.io.File
import java.util.*

@Service
class AttachmentService(
    private val s3Client: AmazonS3,
    private val attachmentDAO: AttachmentDAO
) {

    @Value("\${s3.endpoint}")
    lateinit var endpoint: String

    @Value("\${s3.bucket}")
    lateinit var bucket: String

    private val logger = LoggerFactory.getLogger(AttachmentService::class.java)

    // upload file to s3
    fun upload(upload: MultipartFile): String {
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

        val url = "$endpoint/$bucket/$filename"
        logger.info("File upload finish, md5: ${result.contentMd5}, name: $filename")
        return url
    }

    // delete file form s3
    fun delete(filename: String) {
        // first delete from database
        attachmentDAO.deleteAttachmentByName(filename)
        // then delete from s3
        s3Client.deleteObject(bucket, filename)
        logger.info("File delete, filename: $filename")
    }

    /**
     * Batch delete files from s3
     */
    fun batchDelete(filenames: List<String>) {
        val request = DeleteObjectsRequest(bucket)
        request.withKeys(*filenames.toTypedArray())
        s3Client.deleteObjects(request)
    }

    // upload attachment
    fun uploadAttachment(upload: MultipartFile): AttachmentDTO {
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

        logger.info("Attachment File upload finish, md5: ${result.contentMd5}, name: $filename")
        val dto = AttachmentDTO(
            id = null,
            filename = filename,
            originalFilename = upload.originalFilename,
            url = "$endpoint/$bucket/$filename",
            size = upload.size,
            type = type,
            md5 = result.contentMd5
        )
        return dto
    }

}