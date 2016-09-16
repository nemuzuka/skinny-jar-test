package daemon

import org.apache.commons.daemon.{Daemon, DaemonContext}
import skinny.logging.LoggerProvider

/**
  * jsvcから起動されるClass.
  */
class DaemonMain extends Daemon with LoggerProvider {

  val processor:Thread = new Thread(new DaemonThread())

  /**
    * init処理.
    * @param context context
    */
  override def init(context: DaemonContext): Unit = {
    logger.info("called init.")
    skinny.DBSettings.initialize()
  }

  /**
    * 停止指示受信.
    */
  override def stop(): Unit = {
    logger.info("called stop.")
    processor.interrupt()
    processor.join()
  }

  /**
    * 終了時処理.
    */
  override def destroy(): Unit = logger.info("called destroy.")

  /**
    * 開始処理.
    */
  override def start(): Unit = {
    logger.info("called start.")
    processor.start()
  }
}
