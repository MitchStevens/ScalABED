package test


/**
  * Created by Mitch on 30/01/2017.
  */
import core.types.Expression._
import core.types.Token._
import core.types.{Expression, Signal}
import org.scalatest.FlatSpec

import test.TestExpression.{exp_false, exp_single, exp_true, ins}

class TestExpression extends FlatSpec{

  "A Token" must "initialise without error" in {
    val e1 = Expression("F")
    val e2 = Expression("T")
  }

  it must "have a method is_num that returns true for a number of the form 0x1_ and false otherwise" in {
    for (t: Token <- 0x0 to 0xF)
      assert(! t.is_num, s"$t is not a num")
    for (t: Token <- 0x10 to 0x1F)
      assert(t.is_num, s"$t is a num")
    for (t <- 0x20 to 0xFFF)
      assert(! t.is_num, s"$t is not a num")
  }

  it must "have a method is_bool that returns true for inputs 0 and 1 and false otherwise" in {
    assert(F is_bool, "0 is a bool")
    assert(T is_bool, "1 is a bool")
    for(t: Token <- 0x3 to 0xFFF)
      assert(!t.is_bool, s"$t is not a bool")
  }

  it must "have a step_func method that works as intended" in {
    val step: (Stack, Token) => Stack = step_func(ins)

    assert(step(T::Nil, N) == F::Nil, ". t~ should have evaluated to F")
    assert(step(F::Nil, N) == T::Nil, ". f~ should have evaluated to T")

    for(signal <- Signal.all_of_length(2)){
      val expected_or  = signal(0) | signal(1)
      val expected_and = signal(0) & signal(1)

      assert(step(signal, O).head == expected_or , s". $signal| should have evaluated to $expected_or.")
      assert(step(signal, A).head == expected_and, s". $signal& should have evaluated to $expected_and.")
    }

  }

  it must "return true for the following test cases" in {
    for (exp <- exp_true)
      assert(exp.eval(ins) == T::Nil, s"$exp should have evaluated to T")
  }

  it must "return false for the following test cases" in {
    for (exp <- exp_false)
      assert(exp.eval(ins) == F::Nil, s"$exp should have evaluated to F")
  }

  it must "calculate basic boolean algebra functions" in {
    //Not function
    assert( Expression("T~").eval(ins).head == F, ". T~ should have eval'ed to F")
    assert( Expression("F~").eval(ins).head == T, ". F~ should have eval'ed to T")

    //Or function
    assert( Expression("TT+").eval(ins).head == T, ". TT| should have eval'ed to T")
    assert( Expression("TF+").eval(ins).head == T, ". TF| should have eval'ed to T")
    assert( Expression("FT+").eval(ins).head == T, ". FT| should have eval'ed to T")
    assert( Expression("FF+").eval(ins).head == F, ". FF| should have eval'ed to F")

    //And function
    assert( Expression("TT*").eval(ins).head == T, ". TT& should have eval'ed to T")
    assert( Expression("TF*").eval(ins).head == F, ". TF& should have eval'ed to F")
    assert( Expression("FT*").eval(ins).head == F, ". FT& should have eval'ed to F")
    assert( Expression("FF*").eval(ins).head == F, ". FF& should have eval'ed to F")
  }

  it must "calculate the 'and' function correctly" in {
    for {
      exp1 <- exp_single
      exp2 <- exp_single
    } {
      val t1: Token = exp1.eval(ins)(0) & exp2.eval(ins)(0)
      val t2: Token = (exp1 ++ exp2 ++ Expression("*")) .eval(ins)(0)
      assert(t1 == t2, exp1.toString ++ ". " ++ exp2.toString)
    }
  }

  it must "implement the num_inputs function correctly" in {
    //for (e <- exp_single) {
    // val n: Int = e.num_inputs
    //  assert(n == 0, s". The expression ${e.toString} was found to have $n inputs, expected 0")
    //}
  }

  it must "implement the num_outputs function correctly" in {
    for(e <- exp_single){
      val n: Int = e.num_outputs
    assert(n == 1, s". The expression $e was found to have $n inputs, expected 1.")
    }
  }
}

object TestExpression {

  val ins = F :: T :: F :: T :: Nil
  val exp_true:   Array[Expression] = "T,F~,T~~,TT~+,1,11*,10~*,TT+,TF+,FT+,TT+".split(",") map (Expression(_))
  val exp_false:  Array[Expression] = "F,T~,F~~,FT*,01*,0,0F+,FT*,FF*".split(",") map (Expression(_))
  val exp_single: Array[Expression] = exp_true ++ exp_false
  val exp_other:  Array[Expression] = "T,F,TTFT,0,1,0121,0~,01+,TTTTT+++*".split(",") map (Expression(_))
  val exp_all:    Array[Expression] = exp_true ++ exp_false ++ exp_other

  val eval_zero_inputs: Array[Expression] = "T,F,TTT,F,F~,TFTF*".split(",") map (Expression(_))
}
