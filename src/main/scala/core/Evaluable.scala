package core

import core.Signal.Signal

/**
  * Created by Mitch on 3/18/2017.
  */

trait Evaluable extends Circuit {

  val num_inputs:  Array[Int]
  val num_outputs: Array[Int]
  val last_inputs:  Array[Signal]
  val last_outputs: Array[Signal]

  def set_input(dir: Direction, signal: Signal): Boolean
  def set_inputs(signals: Array[Signal]): Boolean = {
    for (i <- 0 to 3)
      yield set_input(Direction(i), signals(i))
  } reduce (_ && _)

  def calc_outputs(): Unit
}

object Evaluable {
  case object Evaluate
  case object GetInputs
  case object GetOutputs

  case class SetInput(d: Direction, signal: Signal)
  case class SetInputs(signals: Array[Signal])
}