package daemon

import scala.util.control.Breaks._
import java.util.concurrent.{Executors, TimeUnit}

import skinny.logging.LoggerProvider

/**
  * Daemonメイン処理.
  */
class DaemonThread extends Runnable with LoggerProvider {

  /**
    * Thread Pool.
    * ひとまず1つ
    */
  val threadPool = Executors.newFixedThreadPool(1)

  /**
    * Daemonメイン.
    */
  override def run(): Unit = {
    logger.info("called run.")
    breakable {
      while(true) {

        if(Thread.currentThread().isInterrupted) break

        //処理をキューに格納する
        queue()

        if(Thread.currentThread().isInterrupted) break

        //一定時間sleep
        try {
          TimeUnit.SECONDS.sleep(60)
        } catch {
          case e: InterruptedException =>
            break
        }
      }
    }

    //終了処理
    threadPool.shutdown()
    try {
      if(!threadPool.awaitTermination(60, TimeUnit.SECONDS)) threadPool.shutdownNow()
    } catch {
      case e:Throwable => threadPool.shutdownNow
    }
    logger.info("end.")
  }

  /**
    * キューに設定.
    * @return
    */
  def queue():Unit = {
    //本当はデータを設定してTaskを呼び出すと思われる
    val task = new DaemonTask()
    threadPool.submit(task)
  }
}
