package core.circuit

import core.types.Direction
import core.circuit.Port.PortType
import core.types.Signal.Signal
/**
  * Created by Mitch on 5/13/2017.
  */
class Output(val capacity: Int) extends Mapping(s"0,0,0,$capacity", "_,_,_,_") with IOCircuit {

  val port: Port = new Port(PortType.OUT, capacity)
  override def values: Signal = last_inputs(Direction.LEFT)

  override def request_inputs(): Unit = {
    last_inputs(Direction.LEFT) = ports(Direction.LEFT).get_input
  }

  override def calc_outputs(): Unit = {}

  override def send_outputs(): Unit = {
    port set_output this.values
  }

}