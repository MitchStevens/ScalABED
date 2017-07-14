package scalatest

import core.circuit.Input
import org.scalatest.FlatSpec
import testing.TestUtils

/**
  * Created by Mitch on 5/13/2017.
  */
class TestInput extends FlatSpec {

  "An Input" must "be initialised without error" in {
    val input = new Input(1)
  }

  it must "toggle on and off correctly" in {
    val r = scala.util.Random
    for(_ <- 0 until TestUtils.NUM_TESTS){
      val capacity = 1 + r.nextInt(8)
      val idx = r.nextInt(capacity)
      val input: Input = new Input(capacity)
      val last_bools = input.values
      input.toggle(idx)
      val curr_bools = input.values

      for (i <- 0 until capacity)
        if(i == idx)
          assert(last_bools(i) != curr_bools(i))
        else
          assert(last_bools(i) == curr_bools(i))
    }
  }

}
