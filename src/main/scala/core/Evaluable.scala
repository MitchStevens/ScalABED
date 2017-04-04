package core

import core.Signal.Signal

import scala.concurrent.Future

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

  def calc_outputs(): Future[Array[Signal]]
}

object Evaluable {
  case object Evaluate
  case class GetOutput(d: Direction)
  case class SetInput(d: Direction, signal: Signal)
}