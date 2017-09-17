package check

/**
  * Created by Mitch on 7/11/2017.
  */
import core.circuit.Port
import SpecSignal.signal_gen_len
import core.types.Signal.Signal
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

object SpecPort extends Properties("Port") {

  property("instantiation") = forAll { (p: Port) =>
    p != null
  }

  //Generators
  implicit lazy val port_gen: Arbitrary[Port] = Arbitrary(oneOf(
    port_in_gen,
    port_out_gen,
    port_unused_gen
  ))

  val port_in_gen: Gen[Port] =
    Gen.choose(0, 16) map Port.in

  val port_out_gen: Gen[Port] =
    Gen.choose(0, 16) map Port.out

  val port_unused_gen : Gen[Port] = Gen.const(Port.unused)

  val connectable_ports_gen: Gen[(Port, Port)] =
    Gen.choose(0, 16) map {(n: Int) => (Port.create(n, 0), Port.create(0, n))}

}
