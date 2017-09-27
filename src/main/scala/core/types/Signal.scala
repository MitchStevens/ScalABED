package core.types

import cats.Monoid
import core.types.Token._

object Signal {
  type Signal = List[Token]

  implicit class SignalMethods(val signal: Signal) {
    for(token <- signal)
      require(token.is_bool)

    def or:  Boolean = signal.contains(T)
    def and: Boolean = signal.forall(_ == T)

    def str: String = "[" ++ signal.map(_.str).mkString(", ") ++ "]"
  }

  def apply(tokens: Token*): Signal = {
    require(tokens forall (_.is_bool))
    tokens.toList
  }

  def apply(tokens: TraversableOnce[Token]): Signal = {
    require(tokens forall (_.is_bool))
    tokens.toList
  }

  def empty(n: Int): Signal = List.fill(n)(0)

  def all_of_length(n: Int): Seq[Signal] =
    (0 until 1 << n) map int2Signal(n)

  def int2Signal(size: Int)(n: Int): Signal =
    for (i <- 0 until size toList)
      yield (n >> i) & 0x1

}

