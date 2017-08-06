package core.circuit

import core.types.Edge.Edge
import core.types._
import core.types.ID.ID

/**
  * Created by Mitch on 4/23/2017.
  */
class Game(var n: Int) {
  val function: Function = new Function
  val game_info: GameInformation = new GameInformation

  def add(e: Evaluable, pos: Coord): Boolean = {
    val id: ID = ID.generate()
    game_info += (id, GameRecord(pos, Direction.UP, e))
    function.add(id, e)
  }

  def remove(pos: Coord): Option[Evaluable] = {
    for (id <- game_info.id(pos)) {
      game_info -= id
      return function.remove(id)
    }
    None
  }

  def rotate(rotation: Direction, pos: Coord): Boolean = {
    for(id <- game_info.id(pos)) {
      function.disconnect_all(id)
      game_info.update(id, rotation)

      function
    }
    false
  }

  def move(from: Coord, to: Coord): Boolean = {
    false
  }

  def reconnect(id: ID): Unit = {
    //abs_dir = rel_dir + rot
    def reconnect_dir(pos1: Coord, abs_dir: Direction): Unit =
      for {
        id1: ID <- game_info.id(pos1)
        id2: ID <- game_info.id(pos1 + abs_dir)
        rot1    <- game_info.rot(id1)
        rot2    <- game_info.rot(id2)
      } function.connect(
        Edge(id1, abs_dir - rot1),
        Edge(id2, abs_dir - rot2 + 2)
      )

    for {
      pos1 <- game_info.pos(id)
      abs_dir <- Direction.values
    } reconnect_dir(pos1, abs_dir)
  }

  def disconnect(id: ID): Unit = {

  }

}
