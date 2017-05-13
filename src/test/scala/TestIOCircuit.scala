import core.circuit.Input
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/13/2017.
  */
class TestIOCircuit extends FlatSpec {

  "An Input" must "be initialised without error" in {
    val input = new Input(1)
  }

}
