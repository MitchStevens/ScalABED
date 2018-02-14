package check

import core.types.Signal.Signal
import core.types.Token.{F, T, Token}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

/**
  * Created by Mitch on 7/11/2017.
  */
object SpecSignal extends Properties("Signal") with Generators {

  property("generators") = forAll (Gen.choose(0, 100)) {
    (n: Int) => {
      val signal = signal_gen_len(n).sample.get
      signal.lengthCompare(n) == 0
    }
  }

}
