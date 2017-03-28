package core

import javafx.beans.InvalidationListener

import core.Expression._
import core.Signal.Signal


/**
  * Created by Mitch on 3/20/2017.
  */

class Mapping(val logic: Array[Expression]) extends Evaluable {
  require(logic.length == 4)

  val num_inputs:  Array[Int] = logic map (_.num_inputs)
  val num_outputs: Array[Int] = logic map (_.num_outputs)
  val last_inputs:  Array[Signal] = num_inputs  map Signal.empty
  val last_outputs: Array[Signal] = num_outputs map Signal.empty
  calc_outputs()

  override def set_inputs(ins: Array[Signal]): Unit =
    for (d <- Direction.values){
      require(num_inputs(d) == ins(d).length)
      last_inputs(d) = ins(d)
    }

  override def calc_outputs() {
    val flat_inputs: Signal = last_inputs.flatten.toList
    for (d <- 0 to 3)
      last_outputs(d) = logic(d)(flat_inputs)
    this.notifyAll()
  }

  override def receive = {
    case Evaluate  => {}
    case SetInputs => {
//
    }
  }

}

object Mapping {


}