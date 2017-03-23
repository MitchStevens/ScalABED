/**
  * Created by Mitch on 30/01/2017.
  */
import core.Expression
import org.scalatest.FlatSpec

class ExpressionSpecs extends FlatSpec{
  import ExpressionSpecs._

  "A Token" must "initialise without error" in {
    val e1 = new Expression(Array(0))
    val e2 = new Expression(Array(1))
  }

  it must "have non-negative num_input and num_outputs" in {
    for (t <- exp_all) {
      assert(t.num_inputs  >= 0)
      assert(t.num_outputs >= 0)
    }
  }

  it must "return true for the following test cases" in {
    for (t <- exp_true) {
      assert(t(ins).size == 1)
      assert(t(ins)(0) == true)
    }
  }

  it must "return false for the following test cases" in {
    for (t <- exp_false) {
      assert(t(ins).size == 1)
      assert(t(ins)(0) == false)
    }
  }
}

object ExpressionSpecs {
  import core.Expression._

  val ins = F :: T :: F :: T :: Nil
  val exp_true:  Array[Expression]  = "t,f~,t~~,tt~|,f~f|,00~|,11&,10~&"  split "," map to_expression
  val exp_false: Array[Expression] = "f,t~,f~~,ft&,01&,0f|"               split "," map to_expression
  val exp_other: Array[Expression] = "t,f,ttft,0,1,0121,0~,01|,ttttt|||&" split "," map to_expression
  val exp_all:   Array[Expression] = exp_true ++ exp_false ++ exp_other

  def to_expression(str: String): Expression = {
    def token_map(c: Char): Token = c match {
      case 'f' => F
      case 't' => T
      case '~' => N
      case '|' => O
      case '&' => A
      case '0' => 0x10
      case '1' => 0x11
      case '2' => 0x12
      case '3' => 0x13
    }
    new Expression(str.toCharArray map token_map)
  }


}
