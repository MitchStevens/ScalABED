package core

import core.Signal.Signal

import scala.concurrent.Future


/**
  * Created by Mitch on 3/20/2017.
  */

class Mapping(val logic: Array[Expression]) extends Evaluable {
  require(logic.length == 4)

  val num_inputs:  Array[Int] = logic map (_.num_inputs)
  val num_outputs: Array[Int] = logic map (_.num_outputs)
  var last_inputs:  Array[Signal] = num_inputs  map Signal.empty
  var last_outputs: Array[Signal] = num_outputs map Signal.empty
  calc_outputs()

  override def set_input(d: Direction, signal: Signal): Boolean = {
    val same_length = num_inputs(d) == signal.length
    require(same_length)
    last_inputs(d) = signal
    same_length
  }

  override def calc_outputs(): Future[Array[Signal]] = Future {
    val flat_inputs: Signal = last_inputs.flatten toList
    for (d <- 0 to 3)
      println(d)//last_outputs(d) = logic(d)(flat_inputs)
    last_outputs
  }

  override def receive = {
    case Evaluate => calc_outputs()
  }

}

object Mapping {


}