package check

import core.types.Expression._
import org.scalacheck._
import org.scalacheck.Prop.forAll

import generators.GenExpression

/**
  * Created by Mitch on 7/11/2017.
  */
object SpecExpression extends Properties("Expression") with GenExpression {

  property("instantiation") = forAll { (exp: Expression) =>
    exp != null
  }
  /*
  property("output-num") = Prop.forAll (Gen.chooseNum(1, 16)) {
    (n: Int) => for {
      exp <- Monoid[Gen[Expression]].combineN(evaluable_gen(16), n).sample
    } yield exp.num_outputs == n
  }

  property("") = forAll (function_gen, signal_gen_len(16)) {
    (token: Token, signal: Signal) => {
      val f: Expression = List(token)
      val x: Expression = Monoid[Gen[Expression]].combineN(evaluable_gen.combineN(f.num_inputs).sample.get
      (x |+| f).eval(signal) == (x.eval(signal) |+| f).eval(signal)
    }
  }
*/
}