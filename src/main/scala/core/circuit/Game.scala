package core.circuit

import core.types._

/**
  * Created by Mitch on 4/23/2017.
  */
class Game(var n: Int) {
  val function: MesaCircuit = new MesaCircuit
  val game_info: GameInformation = new GameInformation

  def add(e: Evaluable, pos: Coord):            Change[Game] = ???
  def remove(pos: Coord):                       Change[Game] = ???
  def rotate(rotation: Direction, pos: Coord):  Change[Game] = ???
  def move(from: Coord, to: Coord):             Change[Game] = ???

  /*
  def add(e: Evaluable, pos: Coord): Option[AddAction] = {
    val id: ID = ID.generate()
    if (game_info.pos(id).isEmpty && game_info.id(pos).isEmpty) {
      game_info += (id, GameRecord(pos, Direction.UP, e))
      return Some(AddAction(id, pos))
    }
    None
  }

  def remove(pos: Coord): Option[RemoveAction] = {
    for (id <- game_info.id(pos)) {
      game_info -= id
      return Some(RemoveAction(id, pos))
    }
    None
  }

  def rotate(rotation: Direction, pos: Coord): Option[RotateAction] = {
    for (id <- game_info.id(pos)) {
      function.disconnect_all(id)
      game_info.update(id, rotation)
      return Some(RotateAction(id, pos, rotation))
    }
    None
  }

  def move(from: Coord, to: Coord): Option[MoveAction] = {
    for (id <- game_info.id(from))
      if (game_info.id(to).isEmpty) {
        game_info.update(id, to)
        return Some(MoveAction(id, from, to))
      }
    None
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
  */
}
