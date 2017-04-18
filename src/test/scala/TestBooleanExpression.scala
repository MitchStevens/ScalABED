/**
  * Created by Mitch on 30/01/2017.
  */
import core.Expression
import org.scalatest.FlatSpec
import core.Expression._
import core.implicits.Signal
class TestExpression extends FlatSpec{
  import TestExpression._

  "A Token" must "initialise without error" in {
    val e1 = new Expression(0::Nil)
    val e2 = new Expression(1::Nil)
  }

  it must "have a method is_num that returns true for a number of the form 0x1_ and false otherwise" in {
    for (t <- 0x0 to 0xF)
      assert(!is_num(t), s"$t is not a num")
    for (t <- 0x10 to 0x1F)
      assert(is_num(t), s"$t is a num")
    for (t <- 0x20 to 0xFFF)
      assert(!is_num(t), s"$t is not a num")
  }

  it must "have a method is_bool that returns true for inputs 0 and 1 and false otherwise" in {
    assert(is_bool(0x0), "0 is a bool")
    assert(is_bool(0x1), "1 is a bool")
    for(t <- 0x3 to 0xFFF)
      assert(!is_bool(t), t.toString ++" is not a bool")
  }

  it must "have a step_func method that works as intended" in {
    val step = step_func(ins)

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
      assert(exp(ins) == T::Nil, exp.toString ++ " should have evaluated to T")
  }

  it must "return false for the following test cases" in {
    for (exp <- exp_false)
      assert(exp(ins) == F::Nil, exp.toString ++ " should have evaluated to F")
  }

  it must "calculate basic boolean algebra functions" in {
    //Not function
    assert( to_exp("t~")(ins).head == F, ". t~ should have eval'ed to F")
    assert( to_exp("f~")(ins).head == T, ". f~ should have eval'ed to T")

    //Or function
    assert( to_exp("tt|")(ins).head == T, ". tt| should have eval'ed to T")
    assert( to_exp("tf|")(ins).head == T, ". tf| should have eval'ed to T")
    assert( to_exp("ft|")(ins).head == T, ". ft| should have eval'ed to T")
    assert( to_exp("ff|")(ins).head == F, ". ff| should have eval'ed to F")

    //And function
    assert( to_exp("tt&")(ins).head == T, ". tt& should have eval'ed to T")
    assert( to_exp("tf&")(ins).head == F, ". tf& should have eval'ed to F")
    assert( to_exp("ft&")(ins).head == F, ". ft& should have eval'ed to F")
    assert( to_exp("ff&")(ins).head == F, ". ff& should have eval'ed to F")
  }

  it must "calculate the 'and' function correctly" in {
    for {
      exp1 <- exp_single
      exp2 <- exp_single
    } {
      val t1: Token = exp1(ins)(0) & exp2(ins)(0)
      val t2: Token = (exp1 ++ exp2 ++ to_exp("&")) (ins)(0)
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
    assert(n == 1, s". The expression ${e.toString} was found to have $n inputs, expected 1.")
    }
  }
}

object TestExpression {

  val ins = F :: T :: F :: T :: Nil
  val exp_true:   Array[Expression] = to_exp_list ("t,f~,t~~,tt~|,1,11&,10~&,tt|,tf|,ft|,tt|")
  val exp_false:  Array[Expression] = to_exp_list ("f,t~,f~~,ft&,01&,0,0f|,ft&,tf&")
  val exp_single: Array[Expression] = exp_true ++ exp_false
  val exp_other:  Array[Expression] = to_exp_list ("t,f,ttft,0,1,0121,0~,01|,ttttt|||&")
  val exp_all:    Array[Expression] = exp_true ++ exp_false ++ exp_other

  val eval_zero_inputs = to_exp_list("t,f,ttt,f,f~,tftf&")

  def to_exp_list(str: String): Array[Expression] = str split "," map to_exp

  def to_exp(str: String): Expression = {
    def token_map(c: Char): Token = c match {
      case 'f' => F
      case 't' => T
      case '~' => N
      case '|' => O
      case '&' => A
      case 'x' => X
      case '_' => Z
      case '0' => 0x10
      case '1' => 0x11
      case '2' => 0x12
      case '3' => 0x13
    }
    new Expression(str.toCharArray map token_map toList)
  }


}
