package core.types

import core.types.Direction._

/**
  * Created by Mitch on 4/18/2017.
  */
class Coord (val coord: (Int, Int)) {
  val x = coord._1
  val y = coord._2

  def +(that: Coord): Coord = Coord(x+that.x, y+that.y)
  def -(that: Coord): Coord = Coord(x-that.x, y-that.y)

  type OrthoVector = (Direction, Int)
  def trail(list: List[OrthoVector]): List[Coord] = list match {
    case (_, 0) :: rest => trail(rest)
    case (d, n) :: rest => this :: ((this + d) trail ((d, n-1) :: rest))
    case Nil            => this :: Nil
  }

  def trail(vector: OrthoVector): List[Coord] =
    List.iterate(this, vector._2)(_ + vector._1)
}

object Coord {
  val ORIGIN: Coord = new Coord(0, 0)
  def apply(x: Int, y: Int)  = new Coord((x, y))
  def apply(pos: (Int, Int)) = new Coord(pos)

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
