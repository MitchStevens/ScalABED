import core.types.{Direction, Signal}
import core.types.Token._
import core.circuit.Mapping
import org.scalatest.FlatSpec


/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec {

  "A Mapping" must "initialise without error" in {
    val m1: Mapping = new Mapping("0,0,0,0", "_,_,_,_")
    for(n <- m1.num_inputs)
      assert(n == 0)
    for(n <- m1.num_outputs)
      assert(n == 0)
  }

  it must "model a bus correctly" in {
    val bus: Mapping = new Mapping("0,0,0,1", "_,0,_,_");
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
    bus.last_inputs(Direction.LEFT) =  Signal(T)
    bus.calc_outputs()
    assert(bus.last_inputs.flatten.toList  == T :: Nil)
    assert(bus.last_outputs.flatten.toList == T :: Nil)
  }

  it must "model a not correctly" in {
    val not: Mapping = new Mapping("0,0,0,1", "_,0~,_,_")
    val expected_inputs  = Array(0, 0, 0, 1)
    val expected_outputs = Array(0, 1, 0, 0)

    for (i <- 0 to 3){
      assert(not.num_inputs(i)   == expected_inputs(i))
      assert(not.num_outputs(i)  == expected_outputs(i))
    }

    assert(not.last_inputs.flatten.toList  == F :: Nil)
    assert(not.last_outputs.flatten.toList == T :: Nil)
    not.last_inputs(Direction.LEFT) = Signal(T)
    not.calc_outputs()
    assert(not.last_inputs.flatten.toList  == T :: Nil)
    assert(not.last_outputs.flatten.toList == F :: Nil)
  }

  it must "model an or correctly" in {
    val or: Mapping = new Mapping("1,0,0,1", "_,01+,_,_")
    val expected_inputs  = Array(1, 0, 0, 1)
    val expected_outputs = Array(0, 1, 0, 0)

    for (i <- 0 to 3){
      assert(or.num_inputs(i)   == expected_inputs(i))
      assert(or.num_outputs(i)  == expected_outputs(i))
    }

    assert(or.last_inputs.flatten.toList  == F :: F :: Nil)
    assert(or.last_outputs.flatten.toList == F :: Nil)

    or.last_inputs(Direction.LEFT) = Signal(T)
    or.calc_outputs()
    assert(or.last_inputs.flatten.toList  == F :: T :: Nil)
    assert(or.last_outputs.flatten.toList == T :: Nil)

    or.last_inputs(Direction.UP) = Signal(T)
    or.calc_outputs()
    assert(or.last_inputs.flatten.toList  == T :: T :: Nil)
    assert(or.last_outputs.flatten.toList == T :: Nil)

  }

  it must "model a half adder correctly" in {
    val adder: Mapping = new Mapping("1,0,0,1", "_,01^,01*,_")
  }

  it must "model two input versions of one input gates" in {
    val bus2: Mapping = new Mapping("0,0,0,2", "_,10,_,_")
    for (signal <- Signal.all_of_length(2)) {
      bus2.ports(Direction.LEFT) set_input signal
      bus2.evaluate()
      assert(bus2.ports(Direction.RIGHT).get_output == signal)
    }

    val and2: Mapping = new Mapping("2,0,0,2", "_,31*02*,_,_")
    for (in1 <- Signal.all_of_length(2))
      for (in2 <- Signal.all_of_length(2)) {
        and2.ports(Direction.UP) set_input in1
        and2.ports(Direction.LEFT) set_input in2
        and2.evaluate()
        val expected_output = Signal(in1(0) & in2(0), in1(1) & in2(1))
        assert(and2.ports(Direction.RIGHT).get_output == expected_output)
      }
  }

}

object TestMapping {

}