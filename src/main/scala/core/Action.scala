package main.scala.core

import core.Logger
import core.types.{Coord, Direction, Side}
import core.types.ID.ID
import io.Level

/**
  * Created by Mitch on 8/8/2017.
  */
trait Action {
  def description: String
  override def toString: String = description

  def send(): Unit = {Logger.add_action(this)}
}

object GameAction {
  abstract class PieceAction(id: ID, name: String) extends Action {
    override def description: String = s"A(n) \'$name\' with id \'${short_id(id)}\' was "
  }

  case class AddAction(id: ID, name: String, to: Coord)                         extends PieceAction(id, name) {
    override def description: String = super.description ++ s"added at $to"
  }
  case class RemoveAction(id: ID, name: String, from: Coord)                    extends PieceAction(id, name) {
    override def description: String = super.description ++ s"removed at $from"
  }
  case class MoveAction(id: ID, name: String, from: Coord, to: Coord)           extends PieceAction(id, name) {
    override def description: String = super.description ++ s"moved from $from to $to"
  }
  case class RotateAction(id: ID, name: String, pos: Coord, rotate: Direction)  extends PieceAction(id, name) {
    override def description: String = super.description ++ s"rotated at $pos, and rotated ${rotate.n} units"
  }
  case class ToggleAction(id: ID, name: String, pos: Coord, a: Any)             extends PieceAction(id, name) {
    override def description: String = super.description ++ s"sent $a to $pos"
  }

  abstract class ConduitAction(from: Coord, to: Coord) extends Action
  case class ConnectionAction(from: Coord, to: Coord)    extends ConduitAction(from, to) {
    override def description: String = s"The piece at $from was connected to the piece at $to"
  }
  case class DisconnectionAction(from: Coord, to: Coord) extends ConduitAction(from, to) {
    override def description: String = s"The piece at $from was disconnected from the piece at $to"
  }

  def short_id(id: ID): String = ".."+ id.substring(29)
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












