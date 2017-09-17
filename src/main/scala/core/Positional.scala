package core

import core.types.Coord

/**
  * Created by Mitch on 8/9/2017.
  */
trait Positional {
  def position: Coord
  def move(pos: Coord): Unit
}
