package core

import javafx.beans.InvalidationListener

import core.Expression._


/**
  * Created by Mitch on 3/20/2017.
  */

class Mapping(val logic: Array[Expression]) extends Evaluable {
  require(logic.length == 4)

  val num_inputs:  Array[Int] = logic map (_.num_inputs)
  val num_outputs: Array[Int] = logic map (_.num_outputs)
  val last_inputs:  Array[Signal] = num_inputs  map empty
  val last_outputs: Array[Signal] = num_outputs map empty
  calc_outputs()

  override def set_inputs(ins: Array[Signal]): Unit =
    for (d <- Direction.values){
      require(num_inputs(d) == ins(d).length)
      last_inputs(d) = ins(d)
    }

  override def calc_outputs() {
    val flat_inputs: Signal = last_inputs.flatten.toList
    for (d <- Direction.values)
      last_outputs(d) = logic(d)(flat_inputs)
    this.notifyAll()
  }

  override def receive = {
    case "eval" => {}
  }

}

object Mapping {


}