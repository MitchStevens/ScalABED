import core.circuit.Port.PortType
import core.circuit.{Function, Input, Mapping, Output, Port}
import core.types.{Direction, Edge, Signal}
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/12/2017.
  */
class TestFunction extends FlatSpec {

  "A function" must "initialise without error" in {
    val f: Function = new Function()
  }

  it must "allow evaluables to be added and removed" in {
    val f: Function = new Function()
    assert(f.size == 0)
    f add ("e1" -> Mapping.BUS)
    assert(f.size == 1)
    f remove "e1"
    assert(f.size == 0)
  }

  it must "simulate the simplest circuit" in {
    val f: Function = new Function()
    val in:  Input  = new Input(1)
    val out: Output = new Output(1)
    f add ("input"  -> in)
    f add ("output" -> out)
    assert(f.connect(
      Edge("input",  Direction.RIGHT),
      Edge("output", Direction.LEFT)
    ))
    assert(in.values  == Signal(0))
    assert(out.values == Signal(0))

    in.toggle(0)
    f.evaluation_list += "input"
    Thread.sleep(100)

    assert(in.values == Signal(1))
    assert(out.values == Signal(1))
  }

  it must "set sides correctly" in {
    val f: Function = new Function()
    val in:  Input  = new Input(1)
    val out: Output = new Output(1)
    f add ("input"  -> in)
    f add ("output" -> out)
    assert(f.connect(
      Edge("input",  Direction.RIGHT),
      Edge("output", Direction.LEFT)
    ))

    assert(f.sides(Direction.UP).isEmpty)
    assert(f.sides(Direction.RIGHT).isEmpty)
    assert(f.sides(Direction.DOWN).isEmpty)
    assert(f.sides(Direction.LEFT).isEmpty)

    assert(f.num_inputs   sameElements Array(0, 0, 0, 0))
    assert(f.num_outputs  sameElements Array(0, 0, 0, 0))
    assert(f.last_inputs  sameElements (Array(0, 0, 0, 0) map Signal.empty))
    assert(f.last_outputs sameElements (Array(0, 0, 0, 0) map Signal.empty))
    assert(f.ports        sameElements Array.fill(4)(new Port(PortType.UNUSED, 0)))

    f.set_side(Direction.LEFT, "input")
    f.set_side(Direction.RIGHT, "output")

    assert(f.sides(Direction.UP).isEmpty)
    assert(f.sides(Direction.RIGHT).isDefined)
    assert(f.sides(Direction.DOWN).isEmpty)
    assert(f.sides(Direction.LEFT).isDefined)

    assert(f.num_inputs   sameElements Array(0, 0, 0, 1))
    assert(f.num_outputs  sameElements Array(0, 1, 0, 0))
    assert(f.last_inputs  sameElements (Array(0, 0, 0, 0) map Signal.empty))
    assert(f.last_outputs sameElements (Array(0, 0, 0, 0) map Signal.empty))
    assert(f.ports(Direction.UP)    == new Port(PortType.UNUSED, 0))
    assert(f.ports(Direction.RIGHT) == new Port(PortType.OUT, 1))
    assert(f.ports(Direction.DOWN)  == new Port(PortType.UNUSED, 0))
    assert(f.ports(Direction.LEFT)  == new Port(PortType.IN, 1))

    f.ports(Direction.LEFT) set_input Signal(1)
    f.request_inputs()
    Thread.sleep(10)

    assert(f.last_inputs sameElements Array(Nil, Nil, Nil, Signal(1)))
    assert(f.sides(Direction.LEFT).isDefined)
    assert(in.values == Signal(1))



  }
}
