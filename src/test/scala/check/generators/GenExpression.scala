package check.generators

import cats.Monoid
import core.types.Expression
import core.types.Expression.Expression
import core.types.Token.{A, F, I, N, O, T, Token, X, Z}
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalacheck.Gen.{listOf, oneOf}

trait GenExpression {
  implicit val arb_expression: Arbitrary[Expression] =
    Arbitrary(listOf(token_gen.arbitrary))

  implicit val shr_expression: Shrink[Expression] =
    Shrink {(exp: Expression) =>
      exp.inits.toStream
    }

  implicit lazy val token_gen: Arbitrary[Token] =
    Arbitrary(Gen.choose(0, 31))

  val bool_gen:     Gen[Token] = Gen.oneOf(F, T)
  val num_gen:      Gen[Token] = Gen.choose(16, 31)
  val func0_gen:    Gen[Token] = Gen.const(Z)

  val function_gen: Gen[Token] = Gen.oneOf(Z, I, N, O, A, X)
  val variable_gen: Gen[Token] = Gen.choose(16, 31)

  //def evaluable_gen(ins: Int, outs: Int): Gen[Expression] = ???

  private val func1_gen: Gen[Expression] =
    for {
      e <- evaluable_gen
      f <- Gen.oneOf(I, N)
    } yield e ++ f

  private val func2_gen: Gen[Expression] =
    for {
      e1 <- evaluable_gen
      e2 <- evaluable_gen
      f  <- Gen.oneOf(O, A, X)
    } yield e1 ++ e2 ++ f

  def evaluable_gen: Gen[Expression] =
    Gen.lzy(
      Gen.frequency(
        (3, bool_gen),
        (3, variable_gen),
        (3, func1_gen),
        (3, func2_gen)
      )
    )

  private implicit val to_exp:         Token  =>     Expression  = _::Nil
  private implicit val to_exp_gen: Gen[Token] => Gen[Expression] = _ map to_exp
}
