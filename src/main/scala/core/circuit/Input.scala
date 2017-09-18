package core.circuit

import core.circuit.Port.PortType
import core.types.{Direction, Expression, Signal}
import core.types.Signal.Signal

/**
  * Created by Mitch on 5/12/2017.
  */
class Input(val capacity: Int) extends Mapping(Array(0, 0, 0, 0), "_,F,_,_".split(",") map (Expression(_))) {
  require(0 < capacity && capacity <= 8, s"The capacity of input must be between 1 and 8; capacity found: $capacity")

  private var booleans: Signal = Signal.empty(capacity)

  def values: Signal = {
    this.booleans
  }

  override def apply(ins: Array[Signal]): Array[Signal] =
    Array(Signal.empty(0), values, Signal.empty(0), Signal.empty(0))

  def toggle(idx: Int): Unit = {
    if (0 <= idx && idx < capacity)
      booleans = booleans.updated(idx, booleans(idx) ^ 1)
  }

  def set(signal: Signal): Unit =
    if (capacity == signal.length){
      booleans = signal
    }

}
