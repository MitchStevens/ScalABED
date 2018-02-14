package check.generators

import core.types.Coord
import org.scalacheck.{Arbitrary, Gen, Shrink}

trait GenCoord {
  def gen_coord(n: Int): Gen[Coord] =
    for {
      i <- Gen.choose(0, n-1)
      j <- Gen.choose(0, n-1)
    } yield Coord(i, j)

  implicit val arb_coord: Arbitrary[Coord] = Arbitrary(gen_coord(10))
  implicit val shr_coord: Shrink[Coord] = Shrink {
    (pos: Coord) =>
      if (pos == Coord.ORIGIN)
        Stream.empty[Coord]
      else {
        val new_pos = Coord(0 max (pos.x-1), 0 max (pos.y-1))
        Coord.over_rect(new_pos).toStream
      }
  }

}
