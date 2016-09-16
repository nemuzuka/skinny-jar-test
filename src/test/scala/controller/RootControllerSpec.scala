package controller

import org.scalatest._
import skinny.test.MockController

class RootControllerSpec extends FunSpec with Matchers {

  describe("RootController") {
    it("shows top page") {
      val controller = new RootController with MockController
      controller.index
      controller.contentType should equal("text/html; charset=utf-8")
      controller.renderCall.map(_.path) should equal(Some("/root/index"))
    }


    val seq = for(index <- 1 to 10) yield {
      Entity(index, s"val=$index")
    }

    val map1 = seq map {v => v.id -> v}
    println(map1.toMap)

    val map2 = for(s <- seq) yield s.id -> s
    println(map2.toMap)
  }

  case class Entity(id:Long, value:String)

}
