package core.circuit

import cats.kernel.Monoid
import core.types.Expression.{Expression, ExpressionMethods}
import core.types.Signal.Signal
import core.types.{Direction, Expression, Signal}


/**
  * Created by Mitch on 3/20/2017.
  */
class Mapping (input_size: Array[Int], val logic: Array[Expression]) extends Evaluable {
  require(input_size.length == 4)
  for (i <- input_size) require(i >= 0)
  require(logic.length == 4)

  def this(ins: String, outs: String) {
    this(ins.split(",") map (_.head.asDigit), Mapping.logic(outs))
  }

  override def num_inputs(dir: Direction): Int = input_size(dir)

  private val num_outputs_cache = logic map (_.num_outputs)
  override def num_outputs(dir: Direction): Int = num_outputs_cache(dir)

  override def apply(ins: Array[Signal]): Array[Signal] = {
    require(ins.length == 4)

    val input_signal: Signal = ins .foldRight(Signal())(_++_)
    val outs: Array[Signal] =
    for(i <- Direction.values)
      yield logic(i).eval(input_signal) //Should this be a zipWith?
    //assume(outs.length == 4)
    outs
  }

  override def clone: Mapping = {
    new Mapping(input_size, logic)
  }

}

object Mapping {

  private def logic(outs: String) =
    for(exp <- outs.split(","))
      yield Expression(exp)

}










//










//