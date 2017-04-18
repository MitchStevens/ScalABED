package core

import Token._

/**
  * Created by Mitch on 4/18/2017.
  */
class Token(val n: Int) extends Ordering[Token]{

  override def +(that: Token): Token = this.n + that.n
  override def -(that: Token): Token = this.n - that.n

  def inputs: Int = this match {
    case I | N => 1
    case O | A | X => 2
    case _ => 0
  }

  def outputs: Int = if (n == Z) 0 else 1

  def characteristic: Int = this.outputs - this.inputs

  def is_num:  Boolean = (0xFFFFFFF0 & n) == 0x10 //the 5th bit is on, all higher bits are off
  def is_bool: Boolean = (0xFFFFFFFE & n) == 0 //bits 2 through 32 are off

  override def toString: String = token_names(n)
}

object Token {
  val F: Token = 0x00
  val T: Token = 0x01
  val I: Token = 0x02
  val N: Token = 0x03
  val O: Token = 0x04
  val A: Token = 0x05
  val X: Token = 0x06
  val Z: Token = 0x07

  private val token_names: Array[String] =
    Array("F", "T", "ID", "NOT", "OR", "AND", "XOR", "",
          "",  "",  "",  "",  "",  "",  "",  "",
          "0", "1", "2", "3", "4", "5", "6", "7",
          "8", "9", "a", "b", "c", "d", "e", "f")

  override def compare(x: Token, y: Token): Int = x.n compareTo y.n

  implicit def int2Token(n: Int): Token = new Token(n)
  implicit def token2Int(t: Token): Int = t.n

}