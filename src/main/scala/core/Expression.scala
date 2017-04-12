package core


/**
  * Created by Mitch on 3/17/2017.
  */
import Expression._
import core.data.Signal
import core.data.Signal.Signal

import scala.annotation.switch

class Expression(val logic: List[Token]) {

  //is there a better way of doing this?
  lazy val num_inputs: Int = {
    val inputs = for {
      token <- logic
      if is_num(token)
    } yield token - 15
    inputs.fold((0))(_ max _)
  }

  lazy val num_outputs: Int =
    logic.map((t: Token) => token_outputs(t) - token_inputs(t)).sum

  def apply(ins: Signal): Signal =
    logic.foldLeft(Signal.empty(0))(step_func(ins))

  def ++(that: Expression): Expression = new Expression(this.logic ++ that.logic)

  override def toString: String = {
    def f(str: String, token: Token): String = token match {
      case F => str ++ " F"
      case T => str ++ " T"
      case N => s"($str NOT )"
      case O => s"($str OR )"
      case A => s"($str AND )"
      case X => s"($str XOR )"
      case Z => str
      case 0x10 => s"$str 0"
      case 0x11 => s"$str 1"
      case 0x12 => s"$str 2"
      case 0x13 => s"$str 3"
    }
    logic.foldLeft("")(f)
  }
}


object Expression {
  type Token = Int
  type Stack = List[Token]

  val F: Token = 0x00
  val T: Token = 0x01
  val I: Token = 0x02
  val N: Token = 0x03
  val O: Token = 0x04
  val A: Token = 0x05
  val X: Token = 0x06
  val Z: Token = 0x07

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

  private val input_map: Array[Int] = Array(
    0, 0, 1, 1, 2, 2, 2, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0
  )
  private def token_inputs(token: Token): Int = input_map(token)

  private def token_outputs(token: Token): Int = 1

  def is_num(t: Token):  Boolean = (0xFFFFFFF0 & t) == 0x10 //the 5th bit is on, all higher bits are off
  def is_bool(t: Token): Boolean = (0xFFFFFFFE & t) == 0 //bits 2 through 32 are off
}













