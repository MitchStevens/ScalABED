package core

import core.Token
import core.Signal

/**
  * Created by Mitch on 4/18/2017.
  */
class Signal(signal: List[Token]) {
  require( signal forall is_bool )

  override def toString: String = {
    val a = signal map Array("F", "T").apply
    a.mkString(" ")
  }

  def apply(n: Int): Token = signal(n)
  def head: Token = signal.head
  def tail: Signal = signal.tail
  def ::(head: Token): Signal = new Signal(head :: signal)

}

object Signal {
  def empty(n: Int): Signal = List.fill(F)(n)

  def all_of_length(n: Int): Seq[Signal] =
    (0 until 1 << n) map int2Signal

  def int2Signal(n: Int): Signal =
    (0 until 16 toList) map (i => (n >> i) & 0x1)

  implicit def list2Signal(signal: List[Token]): Signal = new Signal(signal)
}
