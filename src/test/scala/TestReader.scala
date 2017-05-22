import io.Reader
import org.scalatest.FlatSpec

/**
  * Created by Mitch on 5/21/2017.
  */
class TestReader extends FlatSpec {

  "The Reader" must "read in levels correctly" in {
    assert(Reader.LEVEL_SETS.nonEmpty)
    assert(Reader.LEVELS.nonEmpty)
  }

}
