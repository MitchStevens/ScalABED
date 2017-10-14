package core.types

/**
  * Created by Mitch on 3/17/2017.
  */

import core.types.Signal.Signal
import core.types.Token._

import scala.annotation.switch

object Expression {
  type Expression = List[Token]
  type Stack = Signal

  def apply(str: String): Expression =
    str.toCharArray map token_map toList

  implicit class ExpressionMethods(val logic: Expression){
    val num_inputs: Int = {
      val inputs = for {
        token <- logic
        if token.is_num
      } yield token - 0xF
      if(inputs.isEmpty) 0 else inputs.max
    }

    val num_outputs: Int =
    logic.map(_.characteristic) .sum

    def eval(ins: Signal): Signal = {
      logic.foldLeft(Signal.empty(0))(step_func(ins))
    }

    //would have prefered to simply override toString as is standard, but can't override toString in implicit class
    def str: String = "(" ++ logic.map(_.str).mkString(" ") ++ ")"

    def tseytin_transformation: Expression = ???

    private def subclauses: List[Expression] = ???

  }

  def token_map(c: Char): Token = (c: @switch) match {
    case 'F' => F
    case 'T' => T
    case 'i' => I
    case '~' => N
    case '+' => O
    case '*' => A
    case '^' => X
    case '_' => Z
    case '0' => 0x10
    case '1' => 0x11
    case '2' => 0x12
    case '3' => 0x13
  }

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
      case n =>
        if (n.is_num)
          ins(token - 0x10) :: stack
        else
          throw new Error("Got unexpected token: $n.")
    }
}






















///