import org.scalatest.FlatSpec
import core.{Function, Mapping}

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
    f add ("input" -> Mapping.BUS)
    f add ("output" -> Mapping.)
  }

}
