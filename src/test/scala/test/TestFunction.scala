package test

import core.circuit.{Input, MesaCircuit, Output}
import core.types.{Direction, Side, Signal}
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 9/15/2017.
  */
class TestFunction extends FlatSpec {

  "A Function" should "initialise without error" in {
    val f: MesaCircuit = new MesaCircuit
  }

  it should "add an evaluable correctly" in {
    val f: MesaCircuit = new MesaCircuit
    assert(f.graphs.num_nodes == 0)
    assert(f.graphs.num_edges == 0)

    f.add("in", new Input(1))
    assert(f.graphs.num_nodes == 1)
    assert(f.graphs.num_edges == 0)
    assert(f.graphs.subcircuits.size == 1)
  }

  it should "simulate a bus correctly" in {
    val f: MesaCircuit = new MesaCircuit
    val in = new Input(1)
    f.add("in",  in)
    f.add("out", new Output(1))
    f.set_side(Direction.LEFT, "in")
    f.connect(Side("in", Direction.RIGHT) -> Side("out", Direction.LEFT))

    f(Signal(1))
    println(f.state)
  }

}
