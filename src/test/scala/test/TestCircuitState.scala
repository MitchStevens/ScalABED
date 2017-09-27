package test

import core.circuit.CircuitState
import core.types.{BindMap, Direction, Side, Signal}
import org.scalatest.FlatSpec

class TestCircuitState extends FlatSpec {

  "A CircuitState" must "initialise correctly" in {
    new CircuitState()
  }

  it must "add sides and states without failing" in {
    val m = new BindMap[Char, Int]
    m += 'a' -> 1
    assert(m.size == 1)
    val a = m('a')
    assert(a == 1)
  }

  it must "add dependencies correctly" in {
    val m = new BindMap[Char, Int]()
    m += 'a' -> 0
    m += 'b' -> 1
    m bind 'a' -> 'b'
    assert(m('b') == 0)
  }

  it must "add dependencies some" in {
    val m = new BindMap[Char, Int]()
    m += 'b' -> 1
    m bind 'b' -> 'a'
    assert(m('a') == 1)
  }

  it must "recognise circular dependencies and fail gracefully" in {

    /*
    val state = new CircuitState()
    val a = Side("a", Direction.UP)
    val b = Side("b", Direction.UP)
    state(a) = Signal(0, 1)
    state(b) = Signal(0, 1)
    state.dependant(a -> b)
    state.dependant(a -> b)
    */
  }

}
