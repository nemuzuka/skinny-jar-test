package iq

import scala.collection.mutable
import scala.util.control.Breaks.{breakable,break}

/**
  * Code IQ用のメインクラス.
  */
object CodeIq {

  /** 向き */
  sealed trait Direction
  /** 左向き. */
  case object Left extends Direction
  /** 右向き. */
  case object Right extends Direction
  /** 下向き. */
  case object Down extends Direction
  /** 上向き. */
  case object Up extends Direction

  /** ステータス. */
  sealed trait Status
  /** 前進可能. */
  case object Go extends Status
  /** ゴール. */
  case object Goal extends Status
  /** 進めない / 打ち切り. */
  case object End extends Status

  /**
    * 処理メイン.
    * @return 最小回転数(ゴールできるルートが存在しない場合、-1)
    */
  def execute(m:Int, n:Int, mapStatus:Array[String]):Int = {

    //ゴールしたルート中での最小回転数.
    var minTurnCount = Int.MaxValue
    //分岐情報
    val queue = mutable.Queue[Position]()
    queue.enqueue(Position(m=0, n=0, turnCount=0, direction = Right))

    val puzzleMap = PuzzleMap(m,n,mapStatus)

    while(queue.nonEmpty) {
      val cursor = new Cursor(queue.dequeue(), puzzleMap, minTurnCount, queue)
      breakable{
        while (true) {
          val (status, turnCountOpt) = cursor.move()
          if(status == Goal && minTurnCount > turnCountOpt.get) minTurnCount = turnCountOpt.get
          if(status == End || status == Goal) break
        }
      }
    }
    if(minTurnCount != Int.MaxValue) minTurnCount else -1
  }

  /**
    * カーソルクラス.
    */
  class Cursor(var now:Position, puzzleMap:PuzzleMap, minTurnCount:Int, queue:mutable.Queue[Position]) {
    /**
      * 移動.
      * 1. 現在の位置、直進できるか判定
      * 2. 現在の位置、右回転して移動できるか判定
      * 3. 現在の位置、左回転して移動できるか判定
      *
      * 4. 直進できて、右回転できる場合、右回転をキューにpush
      * 5. (直進できる or 右回転できる)かつ、左回転できる場合、左回転をキューにpush
      *
      * 6. 全て移動できない場合、Endを返す
      * 7. 進む
      * 8. 進んだ先がゴールの場合、Goalと回転数を返す
      * 9. Goを返す
      *
      * @return _1:ステータス、_2:ゴールの場合の回転数(ゴール以外はNone)
      */
    def move():(Status, Option[Int]) = {

      //既にゴールの場合、処理終了
      val straightPosition = moveStraight()
      val rightPosition = moveRight()
      val leftPosition = moveLeft()

      if(straightPosition.isDefined && rightPosition.isDefined) enqueue(rightPosition.get)
      if((straightPosition.isDefined || rightPosition.isDefined) && leftPosition.isDefined) enqueue(leftPosition.get)

      if(puzzleMap.isGoal(now)) {
        (Goal, Option(now.turnCount))
      } else if(straightPosition.isEmpty && rightPosition.isEmpty && leftPosition.isEmpty) {
        (End, None)
      } else {
        now = if(straightPosition.isDefined) straightPosition.get else if(rightPosition.isDefined) rightPosition.get else leftPosition.get
        puzzleMap.updateTurnStatus(now)
        if(puzzleMap.isGoal(now)) (Goal, Option(now.turnCount)) else (Go, None)
      }
    }

    /**
      * キュー追加.
      * @param position 位置
      */
    private def enqueue(position:Position) = {
      puzzleMap.updateTurnStatus(position)
      queue.enqueue(position)
    }

    /**
      * 直進.
      * 現在位置を元に直進した後の位置を取得します。
      * 直進できない or 意味がない位置の場合、Noneを返します
      * @return 結果
      */
    private def moveStraight():Option[Position] = {
      val newPosition = now.moveStraight()
      if(puzzleMap.isMove(newPosition) && minTurnCount > newPosition.turnCount) Option(newPosition) else None
    }

    /**
      * 右回転 + 移動.
      * 現在位置を元に右回転 + 移動した後の位置を取得します。
      * 右回転 + 移動できない or 意味がない位置の場合、Noneを返します
      * @return 結果
      */
    private def moveRight():Option[Position] = {
      val newPosition = now.moveRight()
      if(puzzleMap.isMove(newPosition) && minTurnCount > newPosition.turnCount) Option(newPosition) else None
    }

    /**
      * 左回転 + 移動.
      * 現在位置を元に左回転 + 移動した後の位置を取得します。
      * 左回転 + 移動できない or 意味がない位置の場合、Noneを返します
      * @return 結果
      */
    private def moveLeft():Option[Position] = {
      val newPosition = now.moveLeft()
      if(puzzleMap.isMove(newPosition) && minTurnCount > newPosition.turnCount) Option(newPosition) else None
    }
  }

  /**
    * 題目Map.
    * (0,0)がstart、(m-1, n-1)がgoalを示します
    * @param m 行数 1 <= m
    * @param n 列数 n <= 64
    * @param mapStatus 各行毎の列情報。「.」:移動可能、「#」:移動不可。行区切りは「\n」で、行数分存在することを保証.
    */
  case class PuzzleMap(m:Int, n:Int, mapStatus:Array[String]) {
    val goalIndexM = m-1
    val goalIndexN = n-1

    /**
      * 地図データ.
      * 各位置で行ける場所をtrueとしてm×nの2次元配列として表します
      */
    val map:Array[Array[Boolean]] = {
      val map = Array.ofDim[Boolean](m,n)
      for((line, mIndex) <- mapStatus.zipWithIndex; (char, nIndex) <- line.toCharArray.zipWithIndex) {
        if(char == '.') map(mIndex)(nIndex) = true
      }
      map
    }

    /**
      * 最小回転数情報.
      * 各位置に侵入した最小回転数を保持します。初期値、Int.MaxValue
      */
    val turnStatus:Array[Array[Int]] = {
      val turnStatus = Array.ofDim[Int](m,n)
      for(mIndex <- 0 until m; nIndex <- 0 until n) {
        turnStatus(mIndex)(nIndex) = Int.MaxValue
      }
      turnStatus
    }

    /**
      * 移動できるか？
      * 位置情報が、
      * ・範囲外でない
      * ・地図上行ける場所
      * ・最小回転数 >= 現在の回転数
      * であるかチェックします
      * @param position 位置
      * @return 移動できる場合、true
      */
    def isMove(position: Position):Boolean = {
      if(position.m < 0 || position.n < 0 || position.m > goalIndexM || position.n > goalIndexN ||
        !map(position.m)(position.n) || turnStatus(position.m)(position.n) < position.turnCount) false else true
    }

    /**
      * 最小回転数更新.
      * @param position 位置
      */
    def updateTurnStatus(position:Position):Unit = {
      turnStatus(position.m)(position.n) = position.turnCount
    }

    /**
      * ゴールの位置か？
      * @param position 位置
      * @return ゴールの位置の場合、true
      */
    def isGoal(position: Position):Boolean = {
      if(position.m == goalIndexM && position.n == goalIndexN) true else false
    }
  }

  /**
    * 位置.
    * @param m M軸の位置(0〜)
    * @param n N軸の位置(0〜)
    * @param turnCount 回転数
    * @param direction 向き
    */
  case class Position(m:Int, n:Int, turnCount:Int, direction:Direction) {

    /**
      * 直進.
      */
    def moveStraight() = {
      val (newM, newN) = direction match {
        case Right => (m, n+1)
        case Left => (m, n-1)
        case Up => (m-1, n)
        case Down => (m+1, n)
      }
      this.copy(m = newM, n = newN)
    }

    /**
      * 右回転+移動.
      */
    def moveRight() = {
      val (newM, newN, newDirection) = direction match {
        case Right => (m+1, n, Down)
        case Down => (m, n-1, Left)
        case Left => (m-1, n, Up)
        case Up => (m, n+1, Right)
      }
      val turnCount = if(this.m == 0 && this.n == 0) 0 else this.turnCount + 1
      this.copy(m = newM, n = newN, turnCount = turnCount, direction = newDirection)
    }

    /**
      * 左回転+移動.
      */
    def moveLeft() = {
      val (newM, newN, newDirection) = direction match {
        case Right => (m-1, n, Up)
        case Down => (m, n+1, Right)
        case Left => (m+1, n, Down)
        case Up => (m, n-1, Left)
      }
      val turnCount = if(this.m == 0 && this.n == 0) 0 else this.turnCount + 1
      this.copy(m = newM, n = newN, turnCount = turnCount, direction = newDirection)
    }
  }
}
