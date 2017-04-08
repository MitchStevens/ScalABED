import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.Signal.Signal
import core.{Direction, Expression, Mapping, Signal}
import org.scalatest.FlatSpec

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec {
  import TestMapping._
  import core.Evaluable._
  import scala.concurrent.duration._
  import concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)


  "A Mapping" must "initialise without error" in {
    val m1 = create_mapping("0;0;0;0", "_;_;_;_")
  }
  it must "model a bus correctly" in {
    val bus = create_mapping("0;0;0;1", "_;0;_;_");
    val future = bus ? GetOutput(Direction.UP)
    future onComplete({
      case Success(_) => println("finished")
    })
  }


}

object TestMapping {
  import TestExpression._

  def create_mapping(ins: String, outs: String): ActorRef = {
    val num_ins = ins.split(";") map (_.head.asDigit)
    val exp_array: Array[Expression] = outs.split(";") map to_exp
    Mapping.inst(num_ins, exp_array)
  }

}