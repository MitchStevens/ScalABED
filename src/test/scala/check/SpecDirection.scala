package check

import core.types.Direction
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

import generators.GenDirection

/**
  * Created by Mitch on 7/12/2017.
  */
object SpecDirection extends Properties("Direction") with GenDirection {

  property("instantiation") = forAll { (dir: Direction) =>
    dir != null
  }

  property("addition") = forAll { (a: Int, b: Int) =>
    Direction(a+b) == Direction(a) + Direction(b)
  }

  property("subtraction") = forAll { (a: Int, b: Int) =>
    Direction(a-b) == Direction(a) - Direction(b)
  }

}