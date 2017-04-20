package core

import core.Token._

object Signal {
  type Signal = List[Token]

  implicit class SignalMethods(signal: Signal) {
    for(token <- signal)
      require(token.is_bool)

    override def toString: String = {
      "hello"
    }
  }

  //is Token* already a list?
  def apply(tokens: Token*): Signal = tokens.toList

  def empty(n: Int): Signal = List.fill(n)(0)

  def all_of_length(n: Int): Seq[Signal] =
    (0 until 1 << n) map int2Signal

  def int2Signal(n: Int): Signal =
    for (i <- 0 until 16 toList)
      yield (n >> i) & 0x1

  implicit def signal2String(signal: Signal): String = {
    val temp = signal .map(Array("F", "T").apply) .mkString(" ")
    s"($temp)"
  }
}

