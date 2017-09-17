package test

import core.circuit.Mapping
import core.types.Token._
import core.types.{Direction, Signal}
import org.scalatest.FlatSpec
import TestMapping._

/**
  * Created by Mitch on 9/5/2017.
  */
class TestMapping extends FlatSpec {
  "A Mapping" must "initialise without error" in {
    val m1: Mapping = new Mapping("0,0,0,0", "_,_,_,_")
    for(d <- Direction.values) {
      assert(m1.num_inputs(d)  == 0)
      assert(m1.num_outputs(d) == 0)
    }
  }

  it must "model a bus correctly" in {
    val m: Mapping = new Mapping("0,0,0,1", "_,0,_,_");
    assert(m.num_input_array  sameElements Array(0, 0, 0, 1))
    assert(m.num_output_array sameElements Array(0, 1, 0, 0))
    assert(m.apply(Signal(T)) sameElements Array(e, Signal(T), e, e))
    assert(m.apply(Signal(F)) sameElements Array(e, Signal(F), e, e))
  }

  it must "model a not correctly" in {
    val m: Mapping = new Mapping("0,0,0,1", "_,0~,_,_");
    assert(m.num_input_array  sameElements Array(0, 0, 0, 1))
    assert(m.num_output_array sameElements Array(0, 1, 0, 0))
    assert(m.apply(Signal(T)) sameElements Array(e, Signal(F), e, e))
    assert(m.apply(Signal(F)) sameElements Array(e, Signal(T), e, e))
  }

  it must "model an OR gate correctly" in {
    val m: Mapping = new Mapping("1,0,0,1", "_,01+,_,_")
    assert(m.num_input_array  sameElements Array(1, 0, 0, 1))
    assert(m.num_output_array sameElements Array(0, 1, 0, 0))
    assert(m.apply(Signal(T, T)) sameElements Array(e, Signal(T), e, e))
    assert(m.apply(Signal(F, T)) sameElements Array(e, Signal(T), e, e))
    assert(m.apply(Signal(T, F)) sameElements Array(e, Signal(T), e, e))
    assert(m.apply(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
  }

  it must "model an AND gate correctly" in {
    val m: Mapping = new Mapping("1,0,0,1", "_,01*,_,_")
    assert(m.num_input_array  sameElements Array(1, 0, 0, 1))
    assert(m.num_output_array sameElements Array(0, 1, 0, 0))
    assert(m.apply(Signal(T, T)) sameElements Array(e, Signal(T), e, e))
    assert(m.apply(Signal(F, T)) sameElements Array(e, Signal(F), e, e))
    assert(m.apply(Signal(T, F)) sameElements Array(e, Signal(F), e, e))
    assert(m.apply(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
  }

  it must "model an XOR gate correctly" in {
    val m: Mapping = new Mapping("1,0,0,1", "_,01~*0~1*+,01*,_")
    assert(m.num_input_array  sameElements Array(1, 0, 0, 1))
    assert(m.num_output_array sameElements Array(0, 1, 1, 0))
    assert(m.apply(Signal(T, T)) sameElements Array(e, Signal(F), Signal(T), e))
    assert(m.apply(Signal(F, T)) sameElements Array(e, Signal(T), Signal(F), e))
    assert(m.apply(Signal(T, F)) sameElements Array(e, Signal(T), Signal(F), e))
    assert(m.apply(Signal(F, F)) sameElements Array(e, Signal(F), Signal(F), e))
  }
}

object TestMapping {
  val e = Signal.empty(0)
}
