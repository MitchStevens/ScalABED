import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.{Expression, Mapping}
import org.scalatest.FlatSpec


/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec {
  import TestMapping._
  import core.Evaluable._
  import scala.concurrent.duration._
  import concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(50 millis)


  "A Mapping" must "initialise without error" in {
    val m1: Mapping = create_mapping("0;0;0;0", "_;_;_;_")
  }

  it must "model a bus correctly" in {
    val bus: Mapping = create_mapping("0;0;0;1", "_;0;_;_");
    println(bus.num_inputs)

  }

}

object TestMapping {
  import TestExpression._

  def create_mapping(ins: String, outs: String): Mapping = {
    val num_ins: Array[Int] = ins.split(";") map (_.head.asDigit)
    val exp_array: Array[Expression] = outs.split(";") map to_exp
    new Mapping(num_ins, exp_array)
  }

}