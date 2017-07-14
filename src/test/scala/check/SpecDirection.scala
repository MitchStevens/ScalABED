package check

import GenDirection._

import core.types.Direction
import org.scalacheck.Gen.{listOf, oneOf}
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

/**
  * Created by Mitch on 7/12/2017.
  */
object SpecDirection extends Properties("Direction") {

  property("instantiation") = forAll {
    (dir: Direction) => dir != null
  }

  property("addition") = forAll {
    (a: Int, b: Int) =>
      Direction(a+b) == Direction(a) + Direction(b)
  }

  property("subtraction") = forAll {
    (a: Int, b: Int) =>
      Direction(a-b) == Direction(a) - Direction(b)
  }

}

object GenDirection {

  implicit val direction_arb: Arbitrary[Direction] =
    Arbitrary(oneOf(Direction.values))

}