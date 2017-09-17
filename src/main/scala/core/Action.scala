package main.scala.core

import core.types.{Coord, Direction}
import core.types.ID.ID
import io.Level

/**
  * Created by Mitch on 8/8/2017.
  */
trait Action {
  //Logger.new_action(this)

  def description: String
  override def toString: ID = description
}



object GameAction {
  abstract class GameAction(id: ID) extends Action

  case class AddAction(id: ID, to: Coord)                         extends GameAction(id) {
    override def description: String = s"A piece with ID:\'$id\' was added at $to"
  }
  case class RemoveAction(id: ID, from: Coord)                    extends GameAction(id) {
    override def description: String = s"A piece with ID:\'$id\' was removed at $from"
  }
  case class MoveAction(id: ID, from: Coord, to: Coord)           extends GameAction(id) {
    override def description: String = s"A piece with ID:\'$id\' was moved from $from to $to"
  }
  case class RotateAction(id: ID, pos: Coord, rotate: Direction)  extends GameAction(id) {
    override def description: String = s"A piece with ID:\'$id\' was rotate at $pos, facing $rotate"
  }
  case class ToggleAction(id: ID, pos: Coord)                     extends GameAction(id) {
    override def description: String = s"A piece with ID:\'$id\' was toggled at $pos"
  }
}

object LevelAction {
  abstract class LevelAction(level: Level) extends Action

  case class LevelSet(level: Level)                   extends LevelAction(level) {
    override def description: String = s"The level objective was set to \'$level\'"
  }
  case class LevelCompleted(level: Level, size: Int)  extends LevelAction(level) {
    override def description: String = s"The level \'$level\' was completed at size $size"
  }
}












