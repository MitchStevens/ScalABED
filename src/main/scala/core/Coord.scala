/**
  * Created by Mitch on 3/18/2017.
  */

package core

class Coord private(val x: Int, val y: Int) {


  def +(that: Coord): Coord = new Coord(x+that.x, y+that.y)
  def -(that: Coord): Coord = new Coord(x-that.x, y-that.y)

  type OrthoVector = (Direction, Int)
  def trail(list: List[OrthoVector]): List[Coord] = list match {
    case (d, 0) :: rest => trail(rest)
    case (d, n) :: rest => this :: ((this + d) trail ((d, n-1) :: rest))
    case Nil            => this :: Nil
  }

  def trail(vector: OrthoVector): List[Coord] =
    List.iterate(this, vector._2)(_ + vector._1)
}

object Coord {
  import Direction._

  val ORIGIN: Coord = new Coord(0, 0)

  def apply(x: Int, y: Int)  = new Coord(x, y)
  def apply(pos: (Int, Int)) = new Coord(pos._1, pos._2)

  def over_side(n: Int, d: Direction): Seq[Coord] =
    over_corner(n)(d) trail ((d, n - 1) :: Nil)

  def over_perimeter(n: Int): Seq[Coord] =
    ORIGIN trail (RIGHT, n-1) :: (DOWN, n-1) :: (LEFT, n-1) :: (UP, n-2) :: Nil

  def over_square(n: Int): Seq[Coord] =
    for {
      i <- 0 until n
      j <- 0 until n
    } yield Coord(i, j)

  def over_corner(n:Int): List[Coord] =
    (0,0)::(0,n-1)::(n-1,n-1)::(n-1,0)::Nil map (Coord(_))
}