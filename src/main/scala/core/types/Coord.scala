package core.types

import core.types.Coord.Location.Location
import core.types.Coord._
import core.types.Direction._

/**
  * Created by Mitch on 4/18/2017.
  */
class Coord (val coord: (Int, Int)) {
  val x = coord._1
  val y = coord._2

  def +(b: Coord):  Coord = Coord(x+b.x, y+b.y)
  def -(b: Coord):  Coord = Coord(x-b.x, y-b.y)
  def *(n: Int):    Coord = Coord(x*n, y*n)
  def /(n: Int):    Coord = Coord(x/n, y/n)

  def within(n: Int): Boolean = x < n && y < n

  /*
  * A square of size n has indexes from (0, 0) to (n-1, n-1). If this coord were placed somewhere on the square, would
  * it rest on one of the edges.
  *
  * if(coord is in middle of square somewhere) return 0
  * else if(coord is on an edge) return 1
  * else if(coord is on the corner) return 2
  *
  * */
  def on_side(size: Int): Location = {
    def f(z: Int) = if (z == 0 || z == size-1) 1 else 0
    Location(f(x)+f(y))
  }

  type OrthoVector = (Direction, Int)
  def trail(list: List[OrthoVector]): List[Coord] = list match {
    case (_, 0) :: rest => trail(rest)
    case (d, n) :: rest => this :: ((this + d) trail ((d, n-1) :: rest))
    case Nil            => this :: Nil
  }

  def trail(vector: OrthoVector): List[Coord] =
    List.iterate(this, vector._2)(_ + vector._1)

  def adj(): List[Coord] = Direction.values map (_ + this)

  // gets taxicab distance
  def taxi_dist(that: Coord): Int =
    math.abs(this.x - that.x) + math.abs(this.y - that.y)

  override def toString: String = s"($x, $y)"
  override def equals(obj: scala.Any): Boolean =
    obj match {
      case c: Coord => c.x == x && c.y == y
      case _ => false
    }
  override def hashCode(): Int = ((x+y+1)*(x+y+2))/2 -x-1

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

  def over_corner(n:Int): Seq[Coord] =
    Seq((0,0), (0,n-1), (n-1,n-1), (n-1,0))

  /*
  * Represents the all the positions that are in `over_square(b)` that aren't in `over_square(a)`
  * */
  def over_square_rem(a: Int, b: Int): Seq[Coord] = {
    assert(a <= b)
    over_square(b) filter (!_.within(a))
  }

  implicit def tuple2Coord(tuple: (Int, Int)): Coord = new Coord(tuple)

  object Location extends Enumeration {
    type Location = Value
    val CENTER, EDGE, CORNER = Value
  }
}

