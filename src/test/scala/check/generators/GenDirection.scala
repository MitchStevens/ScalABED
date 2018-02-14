package check.generators

import core.types.Direction
import org.scalacheck.{Arbitrary, Gen, Shrink}

trait GenDirection {
  val gen_direction: Gen[Direction] =
    Gen.oneOf(Direction.values)

  implicit val arb_direction: Arbitrary[Direction] = Arbitrary(gen_direction)
  implicit val shr_direction: Shrink[Direction] = Shrink { (dir: Direction) => dir match {
    case Direction.UP => Stream.empty
    case _            => Stream(Direction.UP)
  }}
}
