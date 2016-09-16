package batch

import org.joda.time.LocalDate
import scalikejdbc.DB
import service.StaffService
import skinny.logging.LoggerProvider

/**
  * バッチメイン処理.
  */
object BatchMain extends App with LoggerProvider {

  skinny.DBSettings.initialize()

  logger.info("BatchMain is called.")
  DB localTx { implicit session =>
    StaffService.createStaff("batchから起動しました " + LocalDate.now)
  }
  logger.info("BatchMain is end.")
}
