package top.goopper.platform.schedule

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import top.goopper.platform.dao.AttachmentDAO
import top.goopper.platform.service.AttachmentService

@Component
class CleanAttachmentTask(
    private val attachmentDAO: AttachmentDAO,
    private val attachmentService: AttachmentService
) {

    private val logger = LoggerFactory.getLogger(CleanAttachmentTask::class.java)

    // clean attachment every day at 2:00
    @Scheduled(cron = "0 0 2 * * ?")
    fun cleanAttachment() {
        logger.info("Start clean attachment")
        val start = System.currentTimeMillis()
        val filenames = attachmentDAO.batchDeleteUnusedAttachment()
        if (filenames.isNotEmpty()) {
            attachmentService.batchDelete(filenames)
        }
        val end = System.currentTimeMillis()
        logger.info("Clean attachment finished, count: ${filenames.size}, time: ${end - start}ms")
    }

}