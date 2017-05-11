import akka.util.Timeout
import core.types.{Direction, Signal}
import core.{Expression, Mapping}
import core.types.Token._
import core.ConcurrencyContext._
import org.scalatest.FlatSpec


/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec {
  import Mapping._

  "A Mapping" must "initialise without error" in {
    val m1: Mapping = create_mapping("0,0,0,0", "_,_,_,_")
    for(n <- m1.num_inputs)
      assert(n == 0)
    for(n <- m1.num_outputs)
      assert(n == 0)
  }

  it must "model a bus correctly" in {
    val bus: Mapping = create_mapping("0,0,0,1", "_,0,_,_");
    val expected_inputs  = Array(0, 0, 0, 1)
    val expected_outputs = Array(0, 1, 0, 0)
    val expected_last_ins  = Array(Signal.empty(0), Signal.empty(0), Signal.empty(0), Signal.empty(1))
    val expected_last_outs = Array(Signal.empty(0), Signal.empty(1), Signal.empty(0), Signal.empty(0))

    for (i <- 0 to 3) {
      assert(bus.num_inputs(i)   == expected_inputs(i))
      assert(bus.num_outputs(i)  == expected_outputs(i))
      assert(bus.last_inputs(i)  == expected_last_ins(i))
      assert(bus.last_outputs(i) == expected_last_outs(i))
    }

    assert(bus.last_inputs.flatten.toList  == F :: Nil)
    assert(bus.last_outputs.flatten.toList == F :: Nil)
    bus.set_input(Direction.LEFT, Signal(T))
    bus.calc_outputs()
    assert(bus.last_inputs.flatten.toList  == T :: Nil)
    assert(bus.last_outputs.flatten.toList == T :: Nil)
  }

  it must "model a not correctly" in {
    val not: Mapping = create_mapping("0,0,0,1", "_,0~,_,_")
    val expected_inputs  = Array(0, 0, 0, 1)
    val expected_outputs = Array(0, 1, 0, 0)

    for (i <- 0 to 3){
      assert(not.num_inputs(i)   == expected_inputs(i))
      assert(not.num_outputs(i)  == expected_outputs(i))
    }

    assert(not.last_inputs.flatten.toList  == F :: Nil)
    assert(not.last_outputs.flatten.toList == T :: Nil)
    not.set_input(Direction.LEFT, Signal(T))
    not.calc_outputs()
    assert(not.last_inputs.flatten.toList  == T :: Nil)
    assert(not.last_outputs.flatten.toList == F :: Nil)
  }

  it must "model an or correctly" in {
    val or: Mapping = create_mapping("1,0,0,1", "_,01|,_,_")
    val expected_inputs  = Array(1, 0, 0, 1)
    val expected_outputs = Array(0, 1, 0, 0)

    for (i <- 0 to 3){
      assert(or.num_inputs(i)   == expected_inputs(i))
      assert(or.num_outputs(i)  == expected_outputs(i))
    }

    assert(or.last_inputs.flatten.toList  == F :: F :: Nil)
    assert(or.last_outputs.flatten.toList == F :: Nil)

    or.set_input(Direction.LEFT, Signal(T))
    or.calc_outputs()
    assert(or.last_inputs.flatten.toList  == F :: T :: Nil)
    assert(or.last_outputs.flatten.toList == T :: Nil)

    or.set_input(Direction.UP, Signal(T))
    or.calc_outputs()
    assert(or.last_inputs.flatten.toList  == T :: T :: Nil)
    assert(or.last_outputs.flatten.toList == T :: Nil)

  }

  it must "model a half adder correctly" in {
    val adder: Mapping = create_mapping("1,0,0,1", "_,01^,01&,_")
  }

}

object TestMapping {

}