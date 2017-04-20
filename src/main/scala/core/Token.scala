package core

object Token {
  type Token = Int
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

  implicit class TokenMethods(val t: Token){
    def inputs: Int = t match {
      case I | N => 1
      case O | A | X => 2
      case _ => 0
    }

    def outputs: Int = if (t == Z) 0 else 1

    def characteristic: Int = this.outputs - this.inputs

    def is_num:  Boolean = (0xFFFFFFF0 & t) == 0x10 //the 5th bit is on, all higher bits are off
    def is_bool: Boolean = (0xFFFFFFFE & t) == 0 //bits 2 through 32 are off

    override def toString: String = token_names(t)
  }
}