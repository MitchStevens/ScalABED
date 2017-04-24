package core

import akka.actor.ActorRef
import akka.actor.Status.Success
import akka.pattern.ask
import core.Port.PortType
import core.Port.PortType._
import core.ConcurrencyContext._
import core.Port._

import scala.concurrent.Await
import scala.concurrent.duration._
import core.Port._
import core.types.Direction
import core.types.Signal.Signal

import scala.concurrent.Await

/**
  * Created by Mitch on 3/18/2017.
  */

trait Evaluable extends Circuit {
  val num_inputs:  Array[Int]
  val num_outputs: Array[Int]
  val last_inputs:  Array[Signal]
  val last_outputs: Array[Signal]
  val ports: Array[ActorRef]

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

    val type_nums: Array[Int] =
      for (port <- this.ports)
        yield Await.result(port ? PortType, 50 millis)
          .asInstanceOf[PortType]
          .id

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

  def create_ports(ins: Array[Int], outs: Array[Int]): Array[ActorRef] = {
    for (i <- 0 to 3 toArray)
      yield Port.create(ins(i), outs(i))
  }

}