package core.circuit

import core.circuit.Port.PortType
import core.types.{Direction, Expression, Signal}
import core.types.Signal.Signal

/**
  * Created by Mitch on 5/12/2017.
  */
class Input(val capacity: Int) extends Mapping(Array(0, 0, 0, 0), "_,F,_,_".split(",") map (Expression(_))) with IOCircuit {
  require(0 < capacity && capacity <= 8, s"The capacity of input must be between 1 and 8; capacity found: $capacity")

  val port: Port = new Port(PortType.IN, capacity)
  private var booleans: Signal = Signal.empty(capacity)

  override def values: Signal = booleans

  def toggle(idx: Int): Unit = {
    if (0 <= idx && idx < capacity)
      booleans = booleans.updated(idx, booleans(idx) ^ 1)
  }

  override def request_inputs(): Unit = {
    booleans = port.get_input
  }

  override def calc_outputs(): Unit = {
    last_outputs(Direction.RIGHT) = booleans
  }

  override def send_outputs(): Unit = {
    ports(Direction.RIGHT) set_output last_outputs(Direction.RIGHT)
  }

}
