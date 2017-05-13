package core.circuit

import akka.actor.ActorRef
import akka.pattern.ask
import core.ConcurrencyContext._
import core.circuit.Port.PortType
import core.circuit.Port.PortType.PortType
import core.types.Signal.Signal

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by Mitch on 3/18/2017.
  */

trait Evaluable extends Circuit {
  val num_inputs:   Array[Int]
  val num_outputs:  Array[Int]
  val last_inputs:  Array[Signal]
  val last_outputs: Array[Signal]
  val ports:        Array[Port]

  def request_inputs: Unit

  def send_outputs: Unit

  def calc_outputs: Unit

  def repr: Array[String] = {
    val arrows: Array[Array[String]] = Array(
      Array("\\/", "/\\", "  "),
      Array(" <",  " >",  "  "),
      Array("/\\", "\\/", "  "),
      Array("> ",  "< ",  "  ")
    )

    val type_nums = ports map (_.port_type.id)

    val sym: Seq[String] =
      for(i <- 0 to 3)
        yield arrows(i)(type_nums(i))

    Array(
      s"  ${sym(0)}  ",
      s"${sym(3)}[]${sym(1)}",
      s"  ${sym(2)}  "
    )
  }
}

object Evaluable {

  def create_ports(ins: Array[Int], outs: Array[Int]): Array[Port] = {
    for (i <- 0 to 3 toArray)
      yield Port.create(ins(i), outs(i))
  }

}