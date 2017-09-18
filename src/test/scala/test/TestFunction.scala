package test

import core.circuit.{Input, Mapping, MesaCircuit, Output}
import core.types.Token._
import core.types.{Direction, Side, Signal}
import io.Reader
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 9/15/2017.
  */
class TestFunction extends FlatSpec {
  val e = Signal.empty(0)
  val not: Mapping = Reader.MAPPINGS("NOT")
  val or:  Mapping = Reader.MAPPINGS("OR")
  val and: Mapping = Reader.MAPPINGS("AND")
  val nor: Mapping = Reader.MAPPINGS("NOR")

  "A Function" must "initialise without error" in {
    val f: MesaCircuit = new MesaCircuit
  }

  it must "add an evaluable correctly" in {
    val f: MesaCircuit = new MesaCircuit
    assert(f.graphs.num_nodes == 0)
    assert(f.graphs.num_edges == 0)

    f.add("in", new Input(1))
    assert(f.graphs.num_nodes == 1)
    assert(f.graphs.num_edges == 0)
    assert(f.graphs.subcircuits.size == 1)
  }

  it must "simulate a bus correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f.add("in",  new Input(1))
    f.add("out", new Output(1))
    f.set_side(Direction.LEFT, "in")
    f.set_side(Direction.RIGHT, "out")
    f.connect(Side("in", Direction.RIGHT) -> Side("out", Direction.LEFT))

    assert(f(Array(e, e, e, Signal(F))) sameElements Array(e, Signal(F), e, e))
    assert(f(Array(e, e, e, Signal(T))) sameElements Array(e, Signal(T), e, e))
  }

  it must "simulate a not gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f.add("in",  new Input(1))
    f.add("not", not)
    f.add("out", new Output(1))
    f.set_side(Direction.LEFT, "in")
    f.set_side(Direction.RIGHT, "out")
    f.connect(Side("in", Direction.RIGHT)  -> Side("not", Direction.LEFT))
    f.connect(Side("not", Direction.RIGHT) -> Side("out", Direction.LEFT))

    assert(f(Array(e, e, e, Signal(F))) sameElements Array(e, Signal(T), e, e))
    assert(f(Array(e, e, e, Signal(T))) sameElements Array(e, Signal(F), e, e))
  }

  it must "simulate an and gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f.add("in1",  new Input(1))
    f.add("in2",  new Input(1))
    f.add("and", and)
    f.add("out", new Output(1))
    f.set_side(Direction.UP,    "in1")
    f.set_side(Direction.LEFT,  "in2")
    f.set_side(Direction.RIGHT, "out")
    f.connect(Side("in1", Direction.RIGHT)  -> Side("and", Direction.UP))
    f.connect(Side("in2", Direction.RIGHT)  -> Side("and", Direction.LEFT))
    f.connect(Side("and", Direction.RIGHT)  -> Side("out", Direction.LEFT))

    assert(f(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(F, T)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(T, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(T, T)) sameElements Array(e, Signal(T), e, e))
  }

  it must "simulate an xor gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f.add_all(List(
      ("in1",  new Input(1)),
      ("not1", not.clone),
      ("and1", and.clone),
      ("in2",  new Input(1)),
      ("not2", not.clone),
      ("and2", and.clone),
      ("or",   or.clone)
    ))
    f.set_side(Direction.UP,    "in1")
    f.set_side(Direction.LEFT,  "in2")
    f.set_side(Direction.RIGHT, "out")
    f.connect(Side("in1",  Direction.RIGHT)  -> Side("not1", Direction.LEFT))
    f.connect(Side("in1",  Direction.RIGHT)  -> Side("and2", Direction.UP))
    f.connect(Side("not1", Direction.RIGHT)  -> Side("and1", Direction.LEFT))
    f.connect(Side("and1", Direction.RIGHT)  -> Side("or", Direction.UP))

    f.connect(Side("in2",  Direction.RIGHT)  -> Side("not2", Direction.LEFT))
    f.connect(Side("in2",  Direction.RIGHT)  -> Side("and1", Direction.UP))
    f.connect(Side("not2", Direction.RIGHT)  -> Side("and2", Direction.LEFT))
    f.connect(Side("and2", Direction.RIGHT)  -> Side("or", Direction.LEFT))

    f.connect(Side("or",   Direction.RIGHT)  -> Side("out", Direction.LEFT))

    assert(f(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(F, T)) sameElements Array(e, Signal(T), e, e))
    assert(f(Signal(T, F)) sameElements Array(e, Signal(T), e, e))
    assert(f(Signal(T, T)) sameElements Array(e, Signal(F), e, e))
  }

  it must "simulate an rs latch correctly" in {
    val f: MesaCircuit = new MesaCircuit()
    f.add_all(List(
      ("r",   new Input(1)),
      ("s",   new Input(1)),
      ("nor1", nor.clone),
      ("nor2", nor.clone),
      ("q",   new Output(1)),
      ("q_bar",  new Output(1))
    ))
    f.set_side(Direction.UP,    "r")
    f.set_side(Direction.RIGHT, "q")
    f.set_side(Direction.DOWN,  "q_bar")
    f.set_side(Direction.LEFT,  "s")
    f.connect(Side("r",    Direction.RIGHT)  -> Side("nor1", Direction.UP))
    f.connect(Side("nor1", Direction.RIGHT)  -> Side("q", Direction.LEFT))
    f.connect(Side("s",    Direction.RIGHT)  -> Side("nor2", Direction.LEFT))
    f.connect(Side("nor2", Direction.RIGHT)  -> Side("q_bar", Direction.LEFT))
    f.connect(Side("nor2", Direction.RIGHT)  -> Side("nor1", Direction.LEFT))
    f.connect(Side("nor1", Direction.RIGHT)  -> Side("nor2", Direction.UP))

    val hold  = Signal(0, 0)
    val reset = Signal(1, 0)
    val set   = Signal(0, 1)
    def is_off() = f.recieved_outputs(f.state) sameElements Array(e, Signal(F), Signal(T), e)
    def is_on()  = f.recieved_outputs(f.state) sameElements Array(e, Signal(T), Signal(F), e)


    assert(is_off)
    f(hold)
    assert(is_off)
    f(reset)
    assert(is_off)
    f(set)
    assert(is_on)
    f(hold)
    assert(is_on)
    f(reset)
    f(reset)
    println(f.state.toString)
  }

}
