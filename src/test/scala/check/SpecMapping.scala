package check

import core.circuit.{Mapping, Port}
import core.types.Expression.Expression
import SpecPort._
import org.scalacheck.{Arbitrary, Gen}

/**
  * Created by Mitch on 8/24/2017.
  */
object SpecMapping {

  //property("a") = forAll () {
  //  (Ma)
  //}
/*
  implicit lazy val mapping_arb: Arbitrary[Mapping] = Arbitrary()

  val mapping_gen: Gen[Mapping] =
    for (ports: Seq[Port] <- Gen.pick(4, port_gen.arbitrary)) {
      val total_inputs = ports.map(_.get_input).sum
      val num_inputs: Array[Int]   = ports .map(_.capacity) .toArray
      val logic: Array[Expression] =
    }

    } yield Mapping(ports map _.capacity toArray, )

  Mapping (val num_inputs: Array[Int], val logic: Array[Expression])
*/
}
