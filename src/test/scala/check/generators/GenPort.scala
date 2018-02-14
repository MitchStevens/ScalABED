package check.generators

import core.circuit.Port
import core.circuit.Port.PortType
import org.scalacheck.{Arbitrary, Gen, Shrink}

trait GenPort {
  val port_in_gen: Gen[Port] =
    Gen.choose(0, 16) map Port.in

  val port_out_gen: Gen[Port] =
    Gen.choose(0, 16) map Port.out

  val port_unused_gen : Gen[Port] = Gen.const(Port.unused)

  val connectable_ports_gen: Gen[(Port, Port)] =
    Gen.choose(0, 16) map {(n: Int) => (Port.create(n, 0), Port.create(0, n))}

  implicit val arb_port: Arbitrary[Port] = Arbitrary(Gen.oneOf(
    port_in_gen,
    port_out_gen,
    port_unused_gen
  ))
  implicit val shr_port: Shrink[Port] = Shrink {
    case Port(PortType.IN,     0) => Stream(Port.unused)
    case Port(PortType.OUT,    0) => Stream(Port.unused)
    case Port(PortType.UNUSED, 0) => Stream.empty
    case Port(PortType.IN,     d) => (0 until d) map Port.in  toStream
    case Port(PortType.OUT,    d) => (0 until d) map Port.out toStream
  }
}
