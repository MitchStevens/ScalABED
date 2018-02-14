package check

import core.types.Coord
import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalacheck.Arbitrary._

import generators.GenCoord

object SpecCoord extends Properties("Coord") with GenCoord {

  property("instantiation") = forAll { (c: Coord) =>
    true
  }


  //Coords are basically vector spaces, testing for vector space axioms
  property("Associativity of addition") = forAll { (a: Coord, b: Coord, c: Coord) =>
    (a + b) + c == a + (b + c)
  }

  property("Commutativity of addition") = forAll { (a: Coord, b: Coord) =>
    a + b == b + a
  }

  property("Identity element of addition") = forAll { (a: Coord) =>
    a + Coord.ORIGIN == a
  }

  property("Inverse elements of addition") = forAll { (a: Coord) =>
    a - a == Coord.ORIGIN
  }

  property("Compatibility of scalar multiplication") = forAll { (a: Int, b: Int, v: Coord) =>
    v * (a * b) == (v * b) * a
  }

  property("Identity element of scalar multiplication") = forAll { (v: Coord) =>
    v * 1 == v
  }

  property("Distributivity of scalar multiplication wrt. vector addition") = forAll { (a: Int, u: Coord, v: Coord) =>
    (u + v) * a == u * a + v * a
  }

  property("Distributivity of scalar multiplication wrt. field addition") = forAll { (a: Int, b: Int, v: Coord) =>
    v * (a + b) == v * a + v * b
  }
}

