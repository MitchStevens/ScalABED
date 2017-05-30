package core.circuit


import core.ConcurrencyContext._
import core.circuit.Port.PortType
import core.circuit.Port.PortType.PortType
import core.types.Direction
import core.types.Edge.Edge
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

  //get all the new inputs in
  def request_inputs(): Unit

  //do something with the new inputs
  def calc_outputs(): Unit

  //return new outputs to the ports
  def send_outputs(): Unit

  //do all the above operations at once
  def evaluate(): Unit = {
    this.request_inputs()
    this.calc_outputs()
    this.send_outputs()
  }

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

  def repr_ports(ports: Array[Port]): String = {
    val f = (d: Direction) =>  s"\t\t${d.toString.padTo(5, ' ')}: ${ports(d)}"
    Direction.values
      .map(f)
      .mkString("\n")
  }

}