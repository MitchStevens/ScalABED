package core

import Mapping._
import akka.actor.ActorRef
import akka.pattern.ask
import core.Port.PortType

import core.data.{Direction, Signal}
import core.data.Signal.Signal
import core.Port.PortType._
import core.ConcurrencyContext._

import scala.concurrent.{Await}
import scala.concurrent.duration._
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

  override def set_input(d: Direction, signal: Signal): Boolean = {
    val same_length = num_inputs(d) == signal.length
    require(same_length)
    last_inputs(d) = signal
    same_length
  }

  override def calc_outputs() {
    val flat_inputs: Signal = last_inputs.flatten.toList
    for (d <- 0 to 3)
      last_outputs(d) = logic(d)(flat_inputs)
  }

  def repr: Array[String] = {
    val arrows: Array[Array[String]] = Array(
      Array("\\/", "/\\", "  "),
      Array(" <",  " >",  "  "),
      Array("/\\", "\\/", "  "),
      Array("> ",  "< ",  "  ")
    )

    val sym: Array[String] =
      for(i <- 0 to 3 toArray)
        yield Await.result(ports(i) ? PortType, 50 millis) match {
          case IN     => arrows(i)(0)
          case OUT    => arrows(i)(1)
          case UNUSED => arrows(i)(2)
        }

    Array(
      s"  ${sym(0)}  ",
      s"${sym(3)}[]${sym(1)}",
      s"  ${sym(2)}  "
    )
  }


}


object Mapping {

  def create_ports(ins: Array[Int], outs: Array[Int]): Array[ActorRef] = {
    println(ins.mkString(", "))
    println(outs.mkString(", "))
    for (i <- 0 to 3 toArray)
      yield Port.create(ins(i), outs(i))
  }

}










//