package check.generators

import core.types.Signal.Signal
import org.scalacheck.{Arbitrary, Gen, Shrink}
import core.types.Token.{F, T}

trait GenSignal {
  val signal_gen_len: Int => Gen[Signal] = (n: Int) => Gen.listOfN(n, Gen.oneOf(F, T))
  val gen_signal: Gen[Signal] = Gen.choose(1, 32) flatMap signal_gen_len

  implicit val arb_signal: Arbitrary[Signal] = Arbitrary (Gen.choose(0, 100) flatMap signal_gen_len)
  implicit val shr_signal: Shrink[Signal] = Shrink { _.inits.toStream }
}
