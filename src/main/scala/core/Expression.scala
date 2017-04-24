package core


/**
  * Created by Mitch on 3/17/2017.
  */

import core.types.Token._
import Expression._
import core.types.Signal
import core.types.Signal.Signal

import scala.annotation.switch

class Expression(val logic: List[Token]) {
  val num_inputs: Int = {
    val inputs = for {
      token <- logic
      if token.is_num
    } yield (token - 15)
    if(inputs.isEmpty) 0 else (inputs.max)
  }

  val num_outputs: Int =
    logic.map(_.characteristic) .sum

  def apply(ins: Signal): Signal =
    logic.foldLeft(Signal.empty(0))(step_func(ins))

  def ++(that: Expression): Expression = new Expression(this.logic ++ that.logic)

  override def toString: String =
    logic.map(_.toString).mkString(" ")
}


object Expression {

  type Stack = Signal

  def step_func(ins: Signal): (Stack, Token) => Stack =
    (stack: Stack, token: Token) => (token: @switch) match {
      case F => F :: stack
      case T => T :: stack
      case I => stack.head :: stack.tail
      case N => (stack.head ^ 1) :: stack.tail
      case O => (stack(0) | stack(1)) :: (stack drop 2)
      case A => (stack(0) & stack(1)) :: (stack drop 2)
      case X => (stack(0) ^ stack(1)) :: (stack drop 2)
      case Z => stack
      case 0x10 | 0x11 | 0x12 | 0x13 => ins(token - 0x10) :: stack
      case _ => throw new Error("Got unexpected token: $token.")
    }
}