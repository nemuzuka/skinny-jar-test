package iq

import org.scalatest._

class CodeIqSpec extends FunSpec with Matchers {

  describe("execute") {
    it("2 3のケース") {
      val m = 2
      val n = 3
      val mapStatus = Array(
        "..#",
        "..."
      )
      CodeIq.execute(m, n, mapStatus) should equal (1)
    }

    it("2 3のケース 点対称") {
      val m = 2
      val n = 3
      val mapStatus = Array(
        "...",
        "#.."
      )
      CodeIq.execute(m, n, mapStatus) should equal (1)
    }

    it("5 5のケース") {
      val m = 5
      val n = 5
      val mapStatus = Array(
        "..###",
        "....#",
        "..###",
        "#....",
        "....."
      )
      CodeIq.execute(m, n, mapStatus) should equal (2)
    }

    it("5 5のケース 点対称") {
      val m = 5
      val n = 5
      val mapStatus = Array(
        "....#",
        ".....",
        "#.#..",
        "#.#..",
        "###.."
      )
      CodeIq.execute(m, n, mapStatus) should equal (2)
    }

    it("1 1のケース") {
      val m = 1
      val n = 1
      val mapStatus = Array(".")
      CodeIq.execute(m, n, mapStatus) should equal (0)
    }

    it("10 15のケース") {
      val m = 10
      val n = 15
      val mapStatus = Array(
        "..##.....#.....",
        "..........##...",
        ".##...##..#....",
        "#....#.......#.",
        ".......#.......",
        ".....#.#..#....",
        ".....#.#..#....",
        ".#............#",
        "...#.##....##..",
        "..........#...."
      )
      CodeIq.execute(m, n, mapStatus) should equal (0)
    }

  }
}
