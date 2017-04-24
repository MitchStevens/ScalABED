import akka.actor.ActorRef
import akka.pattern.ask
import core.Port._
import core.{Mapping, Port}
import core.ConcurrencyContext._
import core.types.Token._
import core.types.Signal
import core.types.Signal.Signal
import org.scalatest.FlatSpec

import scala.concurrent.{Await, Future}

/**
  * Created by Mitch on 4/23/2017.
  */
class TestPort extends FlatSpec{

  "A Port" must "initialise without error" in {
    val port: ActorRef = Port.inst(PortType.UNUSED, 0)
  }

  it must "return the correct capacity" in {
    for (i <- 1 to 8){
      val port: ActorRef = Port.inst(PortType.IN, i)
      assert(Await.result(port ? Capacity, future_wait) == i)
    }
  }

  it must "return the correct PortType" in {
    val port_in:     ActorRef = Port.inst(PortType.IN, 1)
    val port_out:    ActorRef = Port.inst(PortType.OUT, 1)
    val port_unused: ActorRef = Port.inst(PortType.UNUSED, 0)
    assert(Await.result(port_in ? PortType,     future_wait) == PortType.IN)
    assert(Await.result(port_out ? PortType,    future_wait) == PortType.OUT)
    assert(Await.result(port_unused ? PortType, future_wait) == PortType.UNUSED)
  }

  it must "get the input in a timely manner" in {
    val signal_length: Int = 12
    val port_out: ActorRef = Port.inst(PortType.OUT, signal_length)
    for (signal <- Signal.all_of_length(signal_length)){
      port_out ! SetOutput(signal)
      val future = port_out ? GetOutput
      assert(Await.result(future, future_wait) == signal)
    }
  }

  it must "connect to port of the same capacity and opposing port types" in {
    for (i <- 1 to 8) {
      val port_in:  ActorRef = Port.inst(PortType.IN, i)
      val port_out: ActorRef = Port.inst(PortType.OUT, i)
      val connection_status = port_out ? ConnectTo(port_in)
      assert(Await.result(connection_status, future_wait) == true)
    }
  }

  it must "not allow connections to buses that do not pass connection criteria" in {
    val port0: ActorRef = Port.inst(PortType.IN, 1)
    val port1: ActorRef = Port.inst(PortType.OUT, 2)
    val port2: ActorRef = Port.inst(PortType.UNUSED, 0)
    val ports = List(port0, port1, port2)
    for {
      p1 <- ports
      p2 <- ports
    } {
      val connection_status = p1 ? ConnectTo(p2)
      assert(Await.result(connection_status, future_wait) == false)
    }

  }

  it must "provide full connection, transmitting, disconnection functionality" in {
    val port_in:  ActorRef = Port.inst(PortType.IN, 4)
    val port_out: ActorRef = Port.inst(PortType.OUT, 4)

    assert(Await.result(port_in ? GetInput, future_wait) == Signal(F, F, F, F))
    assert(Await.result(port_out ? GetOutput, future_wait) == Signal(F, F, F, F))

    port_in ! ConnectTo(port_out)
    val port_in_spouse  = Await.result(port_in  ? GetSpouse, future_wait).asInstanceOf[Option[ActorRef]]
    val port_out_spouse = Await.result(port_out ? GetSpouse, future_wait).asInstanceOf[Option[ActorRef]]
    assert(port_in_spouse.isDefined)
    assert(port_out_spouse.isDefined)

    port_out ? SetOutput(Signal(T, T, T, F))
    Thread.sleep(2) //Need to sleep for the

    assert(Await.result(port_in ? GetInput, future_wait) == Signal(T, T, T, F))
    assert(Await.result(port_out ? GetOutput, future_wait) == Signal(T, T, T, F))

    port_out ! DisconnectFrom(port_in)
    Thread.sleep(2)

    assert(Await.result(port_in ? GetInput, future_wait) == Signal(F, F, F, F))
    assert(Await.result(port_out ? GetOutput, future_wait) == Signal(T, T, T, F))
  }

}

object TestPort {

}