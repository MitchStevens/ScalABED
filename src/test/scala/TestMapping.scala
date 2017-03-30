import core.{Expression, Mapping}
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 3/30/2017.
  */
class TestMapping extends FlatSpec{

    "A Mapping" must "initialise without error" in {
      val m1 = new Mapping(Array(0, 0, 0, 0))
    }
  
}
