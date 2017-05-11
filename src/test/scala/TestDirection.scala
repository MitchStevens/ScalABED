import core.types.Direction
import TestUtils._
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/11/2017.
  */
class TestDirection extends FlatSpec {
  "A Direction" should "be able to be created" in {
    val direction:  Direction = Direction.UP
    val direction2: Direction = Direction(0)
    assert(direction == direction2)
  }

  it should "have basic addition/subtraction capabilities" in {
    val r = scala.util.Random
    for(i <- 0 to NUM_TESTS){
      val n1 = r.nextInt()
      val n2 = r.nextInt()
      assert(Direction(n1 + n2) == Direction(n1) + Direction(n2))
      assert(Direction(n1 - n2) == Direction(n1) - Direction(n2))
    }
  }



}
