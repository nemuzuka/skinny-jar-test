package daemon

import org.joda.time.DateTime
import scalikejdbc.DB
import service.StaffService
import skinny.logging.LoggerProvider

/**
  * タスク処理.
  */
class DaemonTask extends Runnable with LoggerProvider {

  /**
    * メイン処理.
    */
  override def run(): Unit = {
    try {
      logger.info("called DaemonTask")
      execute()
    } catch {
      case e:Throwable => logger.error(e.getMessage, e)
    }
  }

  /**
    * メインロジック
    */
  private def execute() {
    DB localTx { implicit session =>
      StaffService.createStaff("daemonから起動しました " + DateTime.now)
    }
  }
}
