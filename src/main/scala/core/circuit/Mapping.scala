package core.circuit

import akka.actor.ActorRef
import akka.pattern.ask
import core.ConcurrencyContext._
import core.types.Signal.Signal
import core.types.{Direction, Expression, Signal}
import core.circuit.Port._

import scala.concurrent.{Await, Future}
/**
  * Created by Mitch on 3/20/2017.
  */
class Mapping (val num_inputs: Array[Int], val logic: Array[Expression]) extends Evaluable {
  require(num_inputs.length == 4)
  for (i <- num_inputs) require(i >= 0)
  require(logic.length == 4)

  def this(ins: String, outs: String) {
    this(ins.split(",") map (_.head.asDigit), Mapping.logic(outs))
  }

  val num_outputs:  Array[Int] = logic map (_.num_outputs)
  val last_inputs:  Array[Signal] = num_inputs  map Signal.empty
  val last_outputs: Array[Signal] = num_outputs map Signal.empty
  val ports:        Array[Port] =
    (num_inputs zip num_outputs) map Port.create
  calc_outputs()

  override def request_inputs(): Unit = {
    for (i <- 0 to 3)
      last_inputs(i) = ports(i).get_input
  }

  override def calc_outputs(): Unit = {
    val flat_inputs: Signal = last_inputs.flatten.toList
    for (d <- 0 to 3)
      last_outputs(d) = logic(d)(flat_inputs)
  }

  override def send_outputs(): Unit = {
    for(i <- 0 to 3)
      ports(i) set_output last_outputs(i)
    println(s"sent $last_outputs")
  }

}


object Mapping {
  import Expression._

  val BUS:    Mapping = new Mapping("0,0,0,1", "_,0,_,_")
  val NOT:    Mapping = new Mapping("0,0,0,1", "_,0~,_,_")
  val SUPER:  Mapping = new Mapping("0,0,0,1", "0,0,0,_")
  val OR:     Mapping = new Mapping("1,0,0,1", "_,01|,_,_")
  val AND:    Mapping = new Mapping("1,0,0,1", "_,01&,_,_")

  private def logic(outs: String) =
    for(exp <- outs.split(","))
      yield new Expression(exp)
}










//