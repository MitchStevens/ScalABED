package main.scala.core

import core.circuit.Evaluable
import core.types.{Coord, Direction}
import core.types.ID.ID
import io.Level

/**
  * Created by Mitch on 8/8/2017.
  */
trait Action {
  //Logger.new_action(this)
  println(this.description)

  def description: String
  override def toString: ID = description
}



object GameAction {
  abstract class GameAction(id: ID, name: String) extends Action

  case class AddAction(id: ID, name: String, to: Coord)                         extends GameAction(id, name) {
    override def description: String = s"A(n) \'$name\' was added at   $to"
  }
  case class RemoveAction(id: ID, name: String, from: Coord)                    extends GameAction(id, name) {
    override def description: String = s"A(n) \'$name\' was removed at $from"
  }
  case class MoveAction(id: ID, name: String, from: Coord, to: Coord)           extends GameAction(id, name) {
    override def description: String = s"A(n) \'$name\' was moved from $from to $to"
  }
  case class RotateAction(id: ID, name: String, pos: Coord, rotate: Direction)  extends GameAction(id, name) {
    override def description: String = s"A(n) \'$name\' was rotate at $pos, facing $rotate"
  }
  case class ToggleAction(id: ID, name: String, pos: Coord)                     extends GameAction(id, name) {
    override def description: String = s"A(n) \'$name\' was toggled at $pos"
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












