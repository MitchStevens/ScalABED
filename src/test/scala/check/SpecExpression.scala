package check

import cats.implicits._
import GenExpression._
import cats.Monoid
import core.types.{Expression, Signal}
import core.types.Expression.ExpressionMethods
import core.types.Expression.Expression
import core.types.Token.{A, F, I, N, O, T, Token, X, Z}
import org.scalacheck.Gen.{listOf, oneOf}
import org.scalacheck.{Arbitrary, Gen, Prop, Properties}
import org.scalacheck.Prop.{BooleanOperators, forAll}

/**
  * Created by Mitch on 7/11/2017.
  */
object SpecExpression extends Properties("Expression") {

  property("monoid") = Prop.forAll (evaluable_gen) {
    (exp: Expression) => {
      exp != null
    }
  }

  property("output-num") = Prop.forAll {
    (n: Int) => {
      val exp: Expression = evaluable_gen.combineN(n).sample.get
      exp.num_outputs == n
    }
  }

  property("") = forAll (function_gen, expression_) {
    (token: Token) => {
      val f = List(token)
      val x = evaluable_gen.combineN(f.num_inputs)

    }
  }

}

object GenExpression {

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
  private val in_token_gen: Gen[Token] = Gen.oneOf(bool_gen, num_gen)

  private def to_exp(t: Token): Expression = List(t)

  implicit lazy val expression_gen: Arbitrary[Expression] =
    Arbitrary(listOf(token_gen.arbitrary))

  val evaluable_gen: Gen[Expression] = {
    Gen.lzy(
      Gen.frequency(
        (3, bool_gen map to_exp),
        (3, num_gen  map to_exp),
        (3, evaluable_gen |+| (func1_gen map to_exp)),
        (3, evaluable_gen.combineN(2) |+| (func2_gen map to_exp))
      )
    )
  }

  implicit def monoid_gen_expression: Monoid[Gen[Expression]] = new Monoid[Gen[Expression]] {
    override def empty: Gen[Expression] = Gen.const(Expression(""))
    override def combine(gx: Gen[Expression], gy: Gen[Expression]): Gen[Expression] =
      for {x <- gx; y <- gy}
        yield x ::: y
  }
}