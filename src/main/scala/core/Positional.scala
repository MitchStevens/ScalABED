package core

import core.types.Coord

/**
  * Created by Mitch on 8/9/2017.
  */
trait Positional {
  var position: Coord
  def move(pos: Coord): Unit = {this.position = pos}
}
