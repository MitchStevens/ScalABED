package check

/**
  * Created by Mitch on 7/11/2017.
  */
import core.circuit.Port
import core.circuit.Port.PortType
import core.types.Signal
import core.types.Signal.Signal
import org.scalacheck.Gen.oneOf
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

import GenPort._
import GenSignal._

object SpecPort extends Properties("Port") {

  property("instantiation") = forAll { (p: Port) =>
    p != null
  }

  val n = 8
  property("getting input") = Prop.forAll(signal_gen_len(n)) {
    (sig: Signal) => {
      val port_out: Port = Port.create(0, n)
      port_out.set_output(sig)
      port_out.get_output == sig
    }
  }

  property("connection") = Prop.forAll(port_out_gen, port_in_gen) {
    (p1: Port, p2: Port) => {
      p1.connect_to(p2) == (p1.capacity == p2.capacity)
    }
  }

  property("transmission") = Prop.forAll(port_out_gen, port_in_gen) {
    (p1: Port, p2: Port) =>
      (p1 connect_to p2) ==> {
        val signal = signal_gen_len(p1.capacity).sample.get
        p1 set_output signal
        p2.get_input sameElements signal
      }

  }

  property("disconnection") = Prop.forAll(port_out_gen, port_in_gen) {
    (p1: Port, p2: Port) =>
      (p1 connect_to p2) ==> {
        p1 disconnect_from p2
        p2.get_spouse.isEmpty
      }
  }

}

object GenPort {

  implicit lazy val port_gen: Arbitrary[Port] = Arbitrary(oneOf(
    port_in_gen,
    port_out_gen,
    port_unused_gen
  ))

  val port_in_gen: Gen[Port] =
    Gen.choose(0, 16) map {new Port(PortType.IN, _)}

  val port_out_gen: Gen[Port] =
    Gen.choose(0, 16) map {new Port(PortType.OUT, _)}

  val port_unused_gen : Gen[Port] = Gen.const(new Port(PortType.UNUSED, 0))

  val connectable_ports_gen: Gen[(Port, Port)] =
    Gen.choose(0, 16) map {(n: Int) => (Port.create(n, 0), Port.create(0, n))}

}
