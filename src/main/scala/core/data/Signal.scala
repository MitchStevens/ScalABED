package core.data

import core.Expression._

/**
  * Created by Mitch on 3/27/2017.
  */
object Signal {
  type Signal = List[Token]

  def apply(tokens: Token*): Signal = tokens toList

  implicit def toString(signal: Signal): String = {
    val m = Array("F", "T")
    if (signal.length > 0){
      var str = "("++ m(signal.head)
      for (t <- signal.tail)
        str += " "++ m(t)
      str ++")"
    } else "()"
  }

  def empty(n: Int): Signal = List.fill(F)(n)

  def all_of_length(n: Int): Seq[Signal] =
    (0 until 1 << n) map int2Signal

  def int2Signal(n: Int): Signal =
    (0 until 16 toList) map (i => (n >> i) & 0x1)
}