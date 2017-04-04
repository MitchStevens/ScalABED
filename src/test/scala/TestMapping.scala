import akka.actor.Props
import core.{Expression, Mapping, Signal}
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec {
  import TestMapping._

  "A Mapping" must "initialise without error" in {
    val m1 = create_mapping("0;0;0;0", "_;_;_;_")
  }
  it must "model a bus correctly" in {
    val bus = create_mapping("0;0;0;1", "_;0;_;_");
  }


}

object TestMapping {
  import TestExpression._
  import core.Circuit._

  def create_mapping(ins: String, outs: String): Mapping = {
    val num_ins = ins.split(";") map (_.head.asDigit)
    val exp_array: Array[Expression] = outs.split(";") map to_exp
    val props = Props(classOf[Mapping], num_ins, exp_array)
    actor_system.actorOf(props)
  }

}