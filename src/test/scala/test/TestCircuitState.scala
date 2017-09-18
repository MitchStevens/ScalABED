package test

import core.circuit.CircuitState
import core.types.{Direction, Side, Signal}
import org.scalatest.FlatSpec

class TestCircuitState extends FlatSpec {

  "A CircuitState" must "initialise correctly" in {
    new CircuitState()
  }

  it must "add sides and states without failing" in {
    val state = new CircuitState()
    val a = Side("a", Direction.LEFT)
    state(a) = Signal(0, 1)
    assert(state.size == 1)
    val signal = state(a)
    assert(signal.isDefined)
    assert(state(a).get == Signal(0, 1))
  }

  it must "add dependencies correctly" in {
    val state = new CircuitState()
    val a = Side("a", Direction.LEFT)
    val b = Side("b", Direction.RIGHT)
    state(a) = Signal(0, 1)
    state(b) = Signal(1, 0)
    state.dependant(a -> b)
    assert(state(a) == state(b))
  }

  it must "add dependencies some" in {
    val state = new CircuitState()
    val a = Side("a", Direction.LEFT)
    val b = Side("b", Direction.RIGHT)
    state(a) = Signal(0, 1)
    state.dependant(a -> b)
    assert(state(a) == state(b))
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
