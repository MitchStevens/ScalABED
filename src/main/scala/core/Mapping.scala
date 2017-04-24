package core

import akka.actor.ActorRef
import akka.pattern.ask
import core.Port.{GetInput, SetOutput}
import core.types.{Direction, Signal}
import core.types.Signal.Signal
import core.Evaluable._
import core.ConcurrencyContext._

import scala.concurrent.{Await, Future}
/**
  * Created by Mitch on 3/20/2017.
  */
class Mapping (val num_inputs: Array[Int], val logic: Array[Expression]) extends Evaluable {
  require(num_inputs.length == 4)
  for (i <- num_inputs) require(i >= 0)
  require(logic.length == 4)

  val num_outputs: Array[Int] = logic map (_.num_outputs)
  val last_inputs:  Array[Signal] = num_inputs  map Signal.empty
  val last_outputs: Array[Signal] = num_outputs map Signal.empty
  val ports: Array[ActorRef] = create_ports(num_inputs, num_outputs)
  calc_outputs()

  //only used for debugging
  def set_input(d: Direction, signal: Signal) {
    last_inputs(d) = signal
  }

  override def request_inputs() {
    val futures: Seq[Future[Signal]] =
      for(i <- 0 to 3)
        yield (ports(i) ? GetInput).asInstanceOf[Future[Signal]]

    for (i <- 0 to 3)
      last_inputs(i) = Await.result(futures(i), future_wait)
  }

  override def send_outputs() {
    for(i <- 0 to 3)
      ports(i) ! SetOutput(last_outputs(i))
  }

  override def calc_outputs() {
    val flat_inputs: Signal = last_inputs.flatten.toList
    for (d <- 0 to 3) {
      last_outputs(d) = logic(d)(flat_inputs)
      ports(d) ! SetOutput(last_outputs(d))
    }
  }

}


object Mapping {

}










//