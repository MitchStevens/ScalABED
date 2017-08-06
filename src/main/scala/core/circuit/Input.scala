package core.circuit

import core.circuit.Port.PortType
import core.types.{Direction, Expression, Signal}
import core.types.Signal.Signal

/**
  * Created by Mitch on 5/12/2017.
  */
class Input(val capacity: Int) extends Mapping(Array(0, 0, 0, 0), "_,F,_,_".split(",") map (Expression(_))) with IOCircuit {
  require(0 < capacity && capacity <= 8, s"The capacity of input must be between 1 and 8; capacity found: $capacity")

  override val eval_number: Int = 1
  val port: Port = Port.in(capacity)
  private var booleans: Signal = Signal.empty(capacity)

  def values: Signal = {
    println(s"getting values: ${ports(Direction.RIGHT)}")
    this.booleans
  }

  def toggle(idx: Int): Unit = {
    if (0 <= idx && idx < capacity)
      booleans = booleans.updated(idx, booleans(idx) ^ 1)
  }

  override def request_inputs(): Unit = {
    booleans = port.get_input
  }

  override def next_state(): Unit = {
    last_outputs(Direction.RIGHT) = booleans
  }

  override def send_outputs(): Unit = {
    println(s"hsh: $booleans")
    ports(Direction.RIGHT) set_output this.booleans
    println(ports(Direction.RIGHT))
  }

}
