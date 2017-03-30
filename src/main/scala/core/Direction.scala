/**
  * Created by Mitch on 26/01/2017.
  */
package core


class Direction private(val n:Int) {

  def +(x: Direction): Direction = Direction(n+x)
  def -(x: Direction): Direction = Direction(n-x)

  override def equals(obj: scala.Any): Boolean =
    if (!obj.isInstanceOf[Direction])
      false
    else
      n == obj.asInstanceOf[Direction].n

  override def hashCode(): Int = n
}

object Direction {
  val UP    = new Direction(0)
  val RIGHT = new Direction(1)
  val DOWN  = new Direction(2)
  val LEFT  = new Direction(3)

  def apply(n: Int): Direction = new Direction(n&3)

  val values: List[Direction] =
    List(UP, RIGHT, DOWN, LEFT)

  implicit def direction2Int(d: Direction): Int = d.n
  implicit def direction2Coord(d: Direction): Coord = d.n match {
    case 0 => Coord(0, -1)
    case 1 => Coord(1, 0)
    case 2 => Coord(0, 1)
    case 3 => Coord(-1, 0)
  }
}