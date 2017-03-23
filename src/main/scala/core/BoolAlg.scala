package core

import scala.annotation.switch
import scala.collection.JavaConverters._

/**
  * Created by Mitch on 3/17/2017.
  */
import Expression._
class Expression(val logic: Array[Token]) {

  //is there a better way of doing this?
  lazy val num_inputs:  Int = {
    val inputs: Array[Token] = for {
      token <- logic
      if (0x10 to 0x1F) contains token
    } yield token - 15
    inputs.fold(0)(_ max _)
  }

  lazy val num_outputs: Int =
    logic.map((t: Token) => token_outputs(t) - token_inputs(t)).sum

  def apply(ins: Signal): Signal =
    logic.foldLeft(List.empty[Token])(step_func(ins))
}

object Expression {
  type Token = Int
  type Stack  = List[Token]
  type Signal = List[Token]

  val F: Token = 0x0
  val T: Token = 0x1
  val N: Token = 0x2
  val O: Token = 0x3
  val A: Token = 0x4

  def empty(capacity: Int): Signal = List.fill(capacity)(F)

  private def step_func(ins: List[Token]): (Stack, Token) => Stack = {
    (stack: Stack, token: Token) => token match {
      case T => T :: stack
      case F => F :: stack
      case N => not (stack.head) :: stack.tail
      case O => or  (stack take 2) :: stack drop 2
      case A => and (stack take 2) :: stack drop 2
      case 0x10 => ins(0) :: stack
      case 0x11 => ins(1) :: stack
      case 0x12 => ins(2) :: stack
      case 0x13 => ins(3) :: stack
    }
  }

  private def token_inputs(token: Token): Int = token match {
    case T | F => 0
    case 0 | 1 | 2 | 3 => 0
    case N => 1
    case O | A => 2
  }

  private def token_outputs(token: Token): Int = 1

  private val bools = F :: T :: Nil
  private def not(t: Token): Token = {
    require(bools contains t)
    t ^ 1
  }

  private def or(t: List[Token]): Token = {
    require(bools contains t(0))
    require(bools contains t(1))
    t(0) | t(1)
  }

  private def and(t: List[Token]): Token = {
    require(bools contains t(0))
    require(bools contains t(1))
    t(0) & t(1)
  }
}















