package scalatest

import core.types.Signal
import core.types.Signal._
import core.types.Token.Token
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/11/2017.
  */
class TestSignal extends FlatSpec {

  "A Signal" must "initalise " in {
    val s1: Signal = Signal()
    val s2: Signal = Signal(0)
    val s3: Signal = Signal(1)
    val s4: Signal = Signal(0, 0)
    val s5: Signal = Signal(1, 1, 1, 0)
    val list = s1::s2::s3::s4::s5::Nil
    for (elem <- list)
      assert(elem != null)
  }

  it must "fail on a signal that is does not have a boolean in it" in {
    assertThrows[java.lang.IllegalArgumentException](Signal(0, 2))
    assertThrows[java.lang.IllegalArgumentException](Signal(-1))
    val r = scala.util.Random
    def non_bool(): Token = {
      val a = r.nextInt(16)*(if (r.nextBoolean()) 1 else -1)
      if(a == 0 || a == 1)
        non_bool()
      else a
    }
    for (_ <- 0 to NUM_TESTS) {
      val random_signal = List.fill(5)(non_bool())
      assertThrows[java.lang.IllegalArgumentException](Signal(random_signal))
    }
  }

  it must "create all possible signals of length n" in {
    for(n <- 1 to 8) {
      val all_signals = Signal.all_of_length(n)
      assert(all_signals.length == 1 << n)
      for (sig <- all_signals)
        assert(sig.length == n)

      val r = scala.util.Random
      for (_ <- 0 to NUM_TESTS) {
        val i1 = r.nextInt(1 << n)
        val i2 = r.nextInt(1 << n)
        if(i1 != i2)
          assert(all_signals(i1) != all_signals(i2))
        else
          assert(all_signals(i1) == all_signals(i2))
      }
    }
  }

  it must "convert from int to signal correctly" in {
    val r = scala.util.Random
    for(_ <- 0 until TestUtils.NUM_TESTS){
      val length = r.nextInt(16)
      val signal = Signal.int2Signal(length)(r.nextInt())
      assert(signal.length == length, s"$signal should have been of length $length.")
    }
  }

  it must "create empty signals correctly" in {
    for(n <- 1 to 16){
      val e = Signal.empty(n)
      assert(e.length == n)
      for(token <- e)
        assert(token == 0)
    }
  }
}
