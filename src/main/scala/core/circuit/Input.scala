package core.circuit

import core.types.{Direction, Expression, Signal}
import core.types.Signal.Signal

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by Mitch on 5/12/2017.
  */
class Input(capacity: Int) extends Mapping(Array(0, 0, 0, 0), "_,F,_,_".split(",") map (new Expression(_))) with IOCircuit {
  private val booleans: Signal = Signal.empty(capacity)

  override def values() = booleans

  def toggle(idx: Int): Unit =
    if (0 <= idx && idx < capacity)
      booleans.updated(idx, booleans(idx) ^ 1)

  override def request_inputs(): Unit = {}

  override def calc_outputs(): Unit = {
    last_outputs(Direction.RIGHT) = booleans
  }

  override def send_outputs(): Unit = {
    ports(Direction.RIGHT) set_output last_outputs(Direction.RIGHT)
  }

}
