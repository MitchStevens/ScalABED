package core.circuit

import core.types.Direction
import core.types.Signal.Signal

/**
  * Created by Mitch on 3/18/2017.
  */

trait Evaluable {
  val name: String

  def num_inputs(dir: Direction): Int
  def num_outputs(dir: Direction): Int
  def ports(dir: Direction): Port = Port.create(num_inputs(dir), num_outputs(dir))

  def num_input_array:  Array[Int]  = Direction.values map num_inputs
  def num_output_array: Array[Int]  = Direction.values map num_outputs

  def apply(ins: Array[Signal]): Array[Signal]
  def apply(signal: Signal): Array[Signal] = {
    val ins: Array[Signal] = Array.ofDim(4)
    var x, d = 0
    for (i <- 0 to 3){
      d = num_inputs(i)
      ins(i) = signal.slice(x, x + d)
      x += d
    }
    apply(ins)
  }

  def rep: Array[String] = {
    val arrows: Array[Array[String]] = Array(
      Array("\\/", "/\\", "  "),
      Array(" <",  " >",  "  "),
      Array("/\\", "\\/", "  "),
      Array("> ",  "< ",  "  ")
    )

    val type_nums =
      for (dir <- Direction.values)
        yield ports(dir).port_type.id

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
    for (i <- Direction.values)
      yield Port.create(ins(i), outs(i))
  }

  def repr_ports(ports: Array[Port]): String = {
    val f = (d: Direction) =>  s"\t\t${d.toString.padTo(5, ' ')}: ${ports(d)}"
    Direction.values
      .map(f)
      .mkString("\n")
  }

  trait EvalException {
    override def toString: String
  }

}