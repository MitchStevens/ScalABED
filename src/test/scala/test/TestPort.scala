package test

import core.circuit.Port
import core.circuit.Port.PortType
import core.types.Signal
import core.types.Token._
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 4/23/2017.
  */
class TestPort extends FlatSpec {

  "A Port" must "initialise without error" in {
    val port: Port = Port.unused
  }

  it must "get the input/output correctly" in {
    val port_in:  Port = Port.create(1, 0)
    val port_out: Port = Port.create(0, 1)
    assert(port_in.get_input   == Signal(0))
    assert(port_in.get_output  == Signal())
    assert(port_out.get_input  == Signal())
    assert(port_out.get_output == Signal(0))
  }

  it must "return the correct capacity" in {
    for (i <- 1 to 8){
      val port: Port = Port.in(i)
      assert(port.capacity == i)
    }
  }

  it must "return the correct PortType" in {
    val port_in:     Port = Port.in(1)
    val port_out:    Port = Port.out(1)
    val port_unused: Port = Port.unused
    assert(port_in.port_type     == PortType.IN)
    assert(port_out.port_type    == PortType.OUT)
    assert(port_unused.port_type == PortType.UNUSED)
  }

  it must "get the input in a timely manner" in {
    val signal_length: Int = 8
    val port_out: Port = Port.out(signal_length)
    for (signal <- Signal.all_of_length(signal_length)){
      port_out.set_output(signal)
      assert(port_out.get_output == signal)
    }
  }

  it must "connect to port of the same capacity and opposing port types" in {
    for (i <- 1 to 8) {
      val port_in:  Port = Port.in(i)
      val port_out: Port = Port.out(i)
     assert(port_out.connect_to(port_in))
    }
  }

  it must "not allow connections to buses that do not pass connection criteria" in {
    val port0: Port = Port.in(1)
    val port1: Port = Port.out(2)
    val port2: Port = Port.unused
    val ports = List(port0, port1, port2)
    for {
      p1 <- ports
      p2 <- ports
    } assert(p1.connect_to(p2) == false)

  }

  it must "provide full connection, transmitting, disconnection functionality" in {
    val port_in:  Port = Port.in(4)
    val port_out: Port = Port.out(4)

    assert(port_in.get_input   == Signal(F, F, F, F))
    assert(port_out.get_output == Signal(F, F, F, F))

    port_out connect_to port_in

    assert(port_in.get_spouse.isDefined)
    assert(port_out.get_spouse.isDefined)

    port_out.set_output(Signal(T, T, T, F))

    assert(port_in.get_input   == Signal(T, T, T, F))
    assert(port_out.get_output == Signal(T, T, T, F))

    port_out disconnect_from port_in

    assert(port_in.get_input   == Signal(F, F, F, F))
    assert(port_out.get_output == Signal(T, T, T, F))
  }
}