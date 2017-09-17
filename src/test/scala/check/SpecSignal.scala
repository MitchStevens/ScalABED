package check

import core.types.Signal.Signal
import core.types.Token.{F, T, Token}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

/**
  * Created by Mitch on 7/11/2017.
  */
object SpecSignal extends Properties("Signal") {

  property("generators") = forAll (Gen.choose(0, 100)) {
    (n: Int) => {
      val signal = signal_gen_len(n).sample.get
      signal.length == n
    }
  }

  //Generators
  implicit lazy val signal_arb: Arbitrary[Signal] = Arbitrary(signal_gen)

  val signal_gen: Gen[Signal] = Gen.choose(1, 32) flatMap signal_gen_len
  def signal_gen_len(n: Int): Gen[Signal] = Gen.listOfN(n, bool_gen)
  val bool_gen: Gen[Token] = Gen.oneOf(F, T)

}
