package check

import cats.{Monad, Monoid}
import cats.implicits._
import core.types.Expression._
import core.types.Signal.Signal
import core.types.Token.{A, F, I, N, O, T, Token, X, Z}
import SpecSignal.signal_gen_len
import core.types.Expression
import org.scalacheck.Gen.{listOf, oneOf}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

/**
  * Created by Mitch on 7/11/2017.
  */
object SpecExpression extends Properties("Expression") {
  property("instantiation") = Prop.forAll (evaluable_gen(16)) {
    (exp: Expression) => {
      exp != null
    }
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
  implicit lazy val token_gen: Arbitrary[Token] =
    Arbitrary(oneOf(
      bool_gen, num_gen,
      func0_gen, func1_gen, func2_gen
    ))

  val bool_gen:     Gen[Token] = Gen.oneOf(F, T)
  val num_gen:      Gen[Token] = Gen.choose(16, 31)
  val func0_gen:    Gen[Token] = Gen.const(Z)
  val func1_gen:    Gen[Token] = Gen.oneOf(I, N)
  val func2_gen:    Gen[Token] = Gen.oneOf(O, A, X)

  val function_gen: Gen[Token] = Gen.oneOf(Z, I, N, O, A, X)
  def variable_gen(n: Int): Gen[Token] = Gen.choose(16, 16+n)

  /*
  * Puzzle
  * */
  def evaluable_gen_(ins: Int, outs: Int): Gen[Expression] = ???

  def evaluable_gen(n: Int): Gen[Expression] =
    Gen.lzy(
      Gen.frequency(
        (3, bool_gen),
        (3, variable_gen(n)),
        (3, evaluable_gen(n) |+| func1_gen),
        (3, evaluable_gen(n) |+| evaluable_gen(n) |+| func2_gen)
      )
    )

  /*
  * convert this to a more general instance for any monoid M, rather than Expression
  * */
  implicit def monoid_gen_expression: Monoid[Gen[Expression]] = new Monoid[Gen[Expression]] {
    override def empty: Gen[Expression] = Gen.const(Expression(""))
    override def combine(gx: Gen[Expression], gy: Gen[Expression]): Gen[Expression] =
      for {x <- gx; y <- gy}
        yield x ::: y
  }

  private implicit val to_exp:         Token  =>     Expression  = _::Nil
  private implicit val to_exp_gen: Gen[Token] => Gen[Expression] = _ map to_exp
  implicit lazy val expression_gen: Arbitrary[Expression] =
    Arbitrary(listOf(token_gen.arbitrary))
}