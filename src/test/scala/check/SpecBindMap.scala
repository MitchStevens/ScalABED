package check

import org.scalatest.FlatSpec

object SpecBindMap extends FlatSpec {

  /**
    * m contains a and m contains b <=> bind (a, b)
    *
    * bind (a, b); unbind (a, b); m == m
    * numbinds < size
    *
    * m bind (a -> b)
    * [Random operations go here]
    *
    * m bind (a -> b)
    * m -= b
    * m get a == None
    *
    */

}
