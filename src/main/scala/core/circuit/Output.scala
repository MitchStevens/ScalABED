package core.circuit

import core.types.{Direction, Expression}
import core.ConcurrencyContext._
import core.types.Signal.Signal
/**
  * Created by Mitch on 5/13/2017.
  */
class Output(capacity: Int) extends Mapping(s"0,0,0,$capacity", "_,_,_,_") with IOCircuit {

  override def values() = last_inputs(Direction.RIGHT)

  override def request_inputs(): Unit = {
    last_inputs(Direction.RIGHT) = ports(Direction.RIGHT).get_input
  }

  override def calc_outputs(): Unit = {}

  override def send_outputs(): Unit = {}

}