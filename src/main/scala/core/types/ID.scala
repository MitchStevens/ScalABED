package core.types

import core.types.Signal.Signal

/**
  * Created by Mitch on 5/16/2017.
  */
object ID {
  private val r = scala.util.Random

  type ID = String
  def generate(): ID = {
    r.alphanumeric.take(32).mkString("")
  }

}