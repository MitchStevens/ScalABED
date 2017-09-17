package core.types

import scala.annotation.switch

/**
  * Created by Mitch on 4/18/2017.
  */
class Direction private(val n: Int) extends Ordered[Direction] {

  def +(x: Direction): Direction = Direction(n+x)
  def -(x: Direction): Direction = Direction(n-x)

  override def toString: String = (n: @switch) match {
    case 0 => "UP"
    case 1 => "RIGHT"
    case 2 => "DOWN"
    case 3 => "LEFT"
  }

  override def compare(that: Direction): Int = this.n - that.n

  override def equals(obj: scala.Any): Boolean = obj match {
    case dir: Direction => dir.n == this.n
    case _              => false
  }

  override def hashCode(): Int = n
}

object Direction {
  val UP    = new Direction(0)
  val RIGHT = new Direction(1)
  val DOWN  = new Direction(2)
  val LEFT  = new Direction(3)

  val values = Array(UP, RIGHT, DOWN, LEFT)

  def apply(n: Int) = new Direction(n&3)

  implicit def direction2Coord(d: Direction): Coord = (d.n: @switch) match {
    case 0 => Coord(0, -1)
    case 1 => Coord(1, 0)
    case 2 => Coord(0, 1)
    case 3 => Coord(-1, 0)
  }

  implicit def direction2Int(d: Direction): Int = d.n
  implicit def int2Direction(i: Int): Direction = Direction(i)
}