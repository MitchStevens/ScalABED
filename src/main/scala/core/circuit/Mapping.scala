package core.circuit

import cats.kernel.Monoid
import core.types.Expression._
import core.types.Signal.Signal
import core.types.{Direction, Expression, Signal}


/**
  * Created by Mitch on 3/20/2017.
  */
class Mapping (input_size: Array[Int], val logic: Array[Expression], override val name: String) extends Evaluable {
  require(input_size.length == 4)
  input_size foreach {i => require(i >= 0)}
  require(logic.length == 4)

  def this(ins: String, outs: String, name: String) {
    this(ins.split(",") map (_.head.asDigit), Mapping.logic(outs), name)
  }

  override def generate_clauses: Unit = ???

  override def num_inputs(dir: Direction): Int = input_size(dir)

  private val num_outputs_cache = logic map (_.num_outputs)
  override def num_outputs(dir: Direction): Int = num_outputs_cache(dir)

  override def apply(ins: Array[Signal]): Array[Signal] = {
    require(ins.length == 4)

    var input_signal = Signal.empty(0)
    for (dir <- Direction.values)
      input_signal = input_signal ++ ins(dir).take(num_inputs(dir))

    val outs: Array[Signal] =
      for(i <- Direction.values)
        yield logic(i).eval(input_signal)
    //assume(outs.length == 4)
    //println(logic.map(_.str).mkString(",") +", inputs: "+ ins.mkString(",") +", outputs: "+ outs.mkString(","))
    outs
  }

  override def clone: Mapping = {
    new Mapping(input_size, logic, name)
  }

}

object Mapping {

  private def logic(outs: String) =
    for(exp <- outs.split(","))
      yield Expression(exp)

}










//










//