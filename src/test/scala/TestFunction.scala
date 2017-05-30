import core.circuit.Port.PortType
import core.circuit.{Function, Input, Mapping, Output, Port}
import io.Reader
import core.types.{Direction, Edge, Signal}
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/12/2017.
  */
class TestFunction extends FlatSpec {
  //number of millis to wait for Function to complete
  val WAIT: Int = 50

  "A function" must "initialise without error" in {
    val f: Function = new Function()
  }

  it must "allow evaluables to be added and removed" in {
    val f: Function = new Function()
    assert(f.size == 0)
    f add ("e1" -> new Mapping("0,0,0,1", "_,0,_,_"))
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

    in.port.set_input(Signal(1))
    assert(in.port.get_input == Signal(1))

    in.toggle(0)
    f.evaluation_list += "input"
    Thread.sleep(WAIT)

    assert(f.evaluation_list.isEmpty)

    assert(in.port.get_input == Signal(1))

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
    assert(f.last_inputs  sameElements Array(Nil, Nil, Nil, Signal(0)))
    assert(f.last_outputs sameElements Array(Nil, Signal(0), Nil, Nil))
    assert(f.ports(Direction.UP)    == new Port(PortType.UNUSED, 0))
    assert(f.ports(Direction.RIGHT) == new Port(PortType.OUT, 1))
    assert(f.ports(Direction.DOWN)  == new Port(PortType.UNUSED, 0))
    assert(f.ports(Direction.LEFT)  == new Port(PortType.IN, 1))

    f.ports(Direction.LEFT) set_input Signal(1)
    f.request_inputs()
    Thread.sleep(WAIT)
    f.send_outputs()

    assert(f.last_inputs sameElements Array(Nil, Nil, Nil, Signal(1)))
    assert(f.sides(Direction.LEFT).isDefined)
    assert(in.values == Signal(1))
    assert(out.values == Signal(1))
  }

  it must "simulate a complete 'AND' gate correctly" in {
    Thread.sleep(1000)
    println("start test")
    val f = new Function()
    val in1 = new Input(1)
    val in2 = new Input(1)
    val and = new Mapping("1,0,0,1", "_,01&,_,_")
    val out = new Output(1)

    f add ("in1" -> in1)
    f add ("in2" -> in2)
    f add ("and" -> and)
    f add ("out" -> out)

    assert(f.connect(
      Edge("in1",  Direction.RIGHT),
      Edge("and",  Direction.LEFT)
    ))
    assert(f.connect(
      Edge("in2",  Direction.RIGHT),
      Edge("and",  Direction.UP)
    ))
    assert(f.connect(
      Edge("and",  Direction.RIGHT),
      Edge("out", Direction.LEFT)
    ))

    f.set_side(Direction.UP,    "in1")
    f.set_side(Direction.LEFT,  "in2")
    f.set_side(Direction.RIGHT, "out")
    Thread.sleep(WAIT)

    //Assert that everything in the function is as it should be after this massive instantiation
    assert(f.sides(Direction.UP).isDefined)
    assert(f.sides(Direction.RIGHT).isDefined)
    assert(f.sides(Direction.DOWN).isEmpty)
    assert(f.sides(Direction.LEFT).isDefined)

    assert(f.num_inputs   sameElements Array(1, 0, 0, 1))
    assert(f.num_outputs  sameElements Array(0, 1, 0, 0))
    assert(f.last_inputs  sameElements Array(Signal(0), Nil, Nil, Signal(0)))
    assert(f.last_outputs sameElements Array(Nil, Signal(0), Nil, Nil))
    assert(f.ports(Direction.UP)    == new Port(PortType.IN, 1))
    assert(f.ports(Direction.RIGHT) == new Port(PortType.OUT, 1))
    assert(f.ports(Direction.DOWN)  == new Port(PortType.UNUSED, 0))
    assert(f.ports(Direction.LEFT)  == new Port(PortType.IN, 1))


    //Test the new 'AND' gate
    //Set one input on. This should still return Signal(0)
    f.ports(Direction.UP) set_input Signal(1)
    f.request_inputs()
    Thread.sleep(WAIT)
    assert(in1.values == Signal(1))
    assert(in2.values == Signal(0))
    assert(f.ports(Direction.RIGHT).get_output == Signal(0))

    //Set the other input on. This should return Signal(1), since both inputs are now on.
    f.ports(Direction.LEFT) set_input Signal(1)
    f.request_inputs()
    Thread.sleep(WAIT)
    f.send_outputs()
    println(f.get_info())
    assert(f.ports(Direction.RIGHT).get_output == Signal(1))

    println("end test")
  }
}
