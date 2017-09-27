package test

import core.circuit.{Input, Mapping, MesaCircuit, Output}
import core.types.Signal.Signal
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

    f += "in" -> new Input(1)
    assert(f.graphs.num_nodes == 1)
    assert(f.graphs.num_edges == 0)
    assert(f.graphs.size == 1)
  }

  it must "simulate a bus correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f ++= List(
      ("in",  new Input(1)),
      ("out", new Output(1))
    )
    f.set_sides(
      (Direction.LEFT, "in"),
      (Direction.RIGHT, "out")
    ).connect_all(
      Side("in", Direction.RIGHT) -> Side("out", Direction.LEFT)
    )

    assert_signals(f(Array(e, e, e, Signal(F))), Array(e, Signal(F), e, e))
    f(Array(e, e, e, Signal(T)))
    //assert_same_signals(f(Array(e, e, e, Signal(T))), Array(e, Signal(T), e, e))
  }

  it must "simulate a not gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f ++= List(
      ("in",  new Input(1)),
      ("not", not),
      ("out", new Output(1))
    )
    f.set_sides(
      (Direction.LEFT, "in"),
      (Direction.RIGHT, "out")
    ).connect_all(
      Side("in", Direction.RIGHT)  -> Side("not", Direction.LEFT),
      Side("not", Direction.RIGHT) -> Side("out", Direction.LEFT)
    )

    assert(f(Array(e, e, e, Signal(F))) sameElements Array(e, Signal(T), e, e))
    assert(f(Array(e, e, e, Signal(T))) sameElements Array(e, Signal(F), e, e))
  }

  it must "simulate an and gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f ++= List(
      ("in1",  new Input(1)),
      ("in2",  new Input(1)),
      ("and", and),
      ("out", new Output(1))
    )
    f.set_sides(
      (Direction.UP,    "in1"),
      (Direction.LEFT,  "in2"),
      (Direction.RIGHT, "out")
    ).connect_all(
      Side("in1", Direction.RIGHT)  -> Side("and", Direction.UP),
      Side("in2", Direction.RIGHT)  -> Side("and", Direction.LEFT),
      Side("and", Direction.RIGHT)  -> Side("out", Direction.LEFT)
    )

    assert(f(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(F, T)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(T, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(T, T)) sameElements Array(e, Signal(T), e, e))
  }

  it must "simulate an xor gate correctly" in {
    val f: MesaCircuit = new MesaCircuit
    f ++= List(
      ("in1",  new Input(1)),
      ("not1", not.clone),
      ("and1", and.clone),
      ("in2",  new Input(1)),
      ("not2", not.clone),
      ("and2", and.clone),
      ("or",   or.clone),
      ("out",  new Output(1))
    )
    f.set_sides(
      (Direction.UP,    "in1"),
      (Direction.LEFT,  "in2"),
      (Direction.RIGHT, "out")
    ).connect_all(
      Side("in1",  Direction.RIGHT) -> Side("not1", Direction.LEFT),
      Side("in1",  Direction.RIGHT) -> Side("and2", Direction.UP),
      Side("not1", Direction.RIGHT) -> Side("and1", Direction.LEFT),
      Side("and1", Direction.RIGHT) -> Side("or", Direction.UP),
      Side("in2",  Direction.RIGHT) -> Side("not2", Direction.LEFT),
      Side("in2",  Direction.RIGHT) -> Side("and1", Direction.UP),
      Side("not2", Direction.RIGHT) -> Side("and2", Direction.LEFT),
      Side("and2", Direction.RIGHT) -> Side("or", Direction.LEFT),
      Side("or",   Direction.RIGHT) -> Side("out", Direction.LEFT)
    )

    assert(f(Signal(F, F)) sameElements Array(e, Signal(F), e, e))
    assert(f(Signal(F, T)) sameElements Array(e, Signal(T), e, e))
    assert(f(Signal(T, F)) sameElements Array(e, Signal(T), e, e))
    assert(f(Signal(T, T)) sameElements Array(e, Signal(F), e, e))
  }

  it must "simulate an rs latch correctly" in {
    val f: MesaCircuit = new MesaCircuit()
    f ++= List(
      "r"     -> new Input(1),
      "s"     -> new Input(1),
      "nor1"  -> nor.clone,
      "nor2"  -> nor.clone,
      "q"     -> new Output(1),
      "q_bar" -> new Output(1)
    )
    f.set_sides(
      (Direction.UP,    "r"),
      (Direction.RIGHT, "q"),
      (Direction.DOWN,  "q_bar"),
      (Direction.LEFT,  "s")
    ).connect_all(
      Side("r",    Direction.RIGHT)  -> Side("nor1", Direction.UP),
      Side("nor1", Direction.RIGHT)  -> Side("q", Direction.LEFT),
      Side("s",    Direction.RIGHT)  -> Side("nor2", Direction.LEFT),
      Side("nor2", Direction.RIGHT)  -> Side("q_bar", Direction.LEFT),
      Side("nor2", Direction.RIGHT)  -> Side("nor1", Direction.LEFT),
      Side("nor1", Direction.RIGHT)  -> Side("nor2", Direction.UP)
    ).evaluate()

    val hold  = Signal(0, 0)
    val reset = Signal(1, 0)
    val set   = Signal(0, 1)
    def is_off() = assert_signals(f.recieved_outputs(f.state), Array(e, Signal(F), Signal(T), e))
    def is_on()  = assert_signals(f.recieved_outputs(f.state), Array(e, Signal(T), Signal(F), e))

    is_on
    f(hold)
    is_off
    f(reset)
    is_off
    f(set)
    is_on
    f(hold)
    is_on
    f(reset)
    f(reset)
  }

  def assert_signals(sig1: Array[Signal], sig2: Array[Signal]): Unit =
    for (dir <- Direction.values)
      assert(sig1(dir) == sig2(dir), s". The signal on the $dir did not match.")

}
