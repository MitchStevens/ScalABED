package test

import core.circuit.Port.PortType
import core.circuit.{Function, Input, Mapping, Output, Port}
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

  it must "set a single side correctly" in {
    val f  = new Function()
    val in = new Input(1)
    f add ("input" -> in)
    f.set_side(Direction.LEFT, "input")

    //make sure that the side is set and that it outputs 0
    assert(f.sides(3).isDefined, "side was not set correctly")
    assert(f.sides(3).get.isInstanceOf[Input], "side was not set as an input")
    f.request_inputs()
    Thread.sleep(WAIT)
    assert(f.ports(3).get_input == Signal(0), "value of input was not F")
    assert(in.values == Signal(0))

    //set the input of the port to on
    f.ports(3).set_input(Signal(1))
    f.request_inputs()
    Thread.sleep(WAIT)
    assert(f.ports(3).get_input == Signal(1))
    assert(in.values == Signal(1))
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
    val b = f.connect(
      Edge("input",  Direction.RIGHT),
      Edge("output", Direction.LEFT)
    )
    assert(b)
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

    //check that all the sides are are empty before we add anything to the function
    for(side <- f.sides)
      assert(side.isEmpty)

    //this function should noit accept any inputs or outputs on any side
    assert(f.num_inputs   sameElements Array(0, 0, 0, 0))
    assert(f.num_outputs  sameElements Array(0, 0, 0, 0))

    //as such, it also shouldn't have any 'last inputs/outputs'. This is represented by the empty signal
    assert(f.last_inputs  sameElements List.fill(4)(Signal.empty(0)))
    assert(f.last_outputs sameElements List.fill(4)(Signal.empty(0)))

    //resting that all the ports are unused
    for(port <- f.ports)
      assert(port == Port.unused)

    //setting values for the sides
    f.set_side(Direction.LEFT, "input")
    f.set_side(Direction.RIGHT, "output")

    //after setting sides, some of the sides should be defined..
    assert(f.sides(Direction.UP).isEmpty)
    assert(f.sides(Direction.RIGHT).isDefined)
    assert(f.sides(Direction.DOWN).isEmpty)
    assert(f.sides(Direction.LEFT).isDefined)

    //..so num inputs/outputs should be set..
    assert(f.num_inputs   sameElements Array(0, 0, 0, 1))
    assert(f.num_outputs  sameElements Array(0, 1, 0, 0))

    //..so last inputs/outputs should be populated with signals of the same length as the num inputs
    assert(f.last_inputs  sameElements Array(Nil, Nil, Nil, Signal(0)))
    assert(f.last_outputs sameElements Array(Nil, Signal(0), Nil, Nil))

    //and as before the ports are set correctly
    assert(f.ports(Direction.UP)    == Port.unused)
    assert(f.ports(Direction.RIGHT) == Port.out(1))
    assert(f.ports(Direction.DOWN)  == Port.unused)
    assert(f.ports(Direction.LEFT)  == Port.in(1))

    //After all this set up, we test the input for the function
    f.ports(Direction.LEFT) set_input Signal(1)

    //Getting new inputs
    f.request_inputs()

    //checking the function got the correct inputs/outputs, before the outputs are sent
    assert(f.last_inputs  sameElements Array(Nil, Nil, Nil, Signal(1)))
    assert(f.last_outputs sameElements Array(Nil, Signal(0), Nil, Nil))

    println(f.evaluation_list)

    Thread.sleep(10*WAIT)
    f.send_outputs()

    //checking the function got the correct inputs/outputs, after the outputs are sent
    assert(f.last_inputs  sameElements Array(Nil, Nil, Nil, Signal(1)))
    assert(f.last_outputs sameElements Array(Nil, Signal(1), Nil, Nil))

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
    val and = new Mapping("1,0,0,1", "_,01*,_,_")
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
    assert(f.ports(Direction.UP)    == Port.in(1))
    assert(f.ports(Direction.RIGHT) == Port.out(1))
    assert(f.ports(Direction.DOWN)  == Port.unused)
    assert(f.ports(Direction.LEFT)  == Port.in(1))


    //Test the new 'AND' gate
    //Set one input on. This should still return Signal(0)
    f.ports(Direction.UP) set_input Signal(1)
    Thread.sleep(WAIT)
    f.request_inputs()
    Thread.sleep(WAIT)
    assert(in1.values == Signal(1))
    assert(in2.values == Signal(0))
    assert(f.ports(Direction.RIGHT).get_output == Signal(0))

    //Set the other input on. This should return Signal(1), since both inputs are now on.
    f.ports(Direction.LEFT) set_input Signal(1)
    Thread.sleep(WAIT)
    f.request_inputs()
    Thread.sleep(WAIT)
    f.send_outputs()
    println(f.get_info())
    assert(f.ports(Direction.RIGHT).get_output == Signal(1))
  }
}
