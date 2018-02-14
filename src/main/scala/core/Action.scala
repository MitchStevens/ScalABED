package main.scala.core

import core.Logger
import core.types.{Coord, Direction, Side}
import core.types.ID.ID
import io.Level

/**
  * Created by Mitch on 8/8/2017.
  */
trait Action {
  private var sent = false
  def send(): Unit =
    if (!sent) {
      Logger.add_action(this)
      sent = true
    }

  def description: String = "ACTION: "
  override def toString: String = description
}

object GameAction {
  val verbose = true
  abstract class PieceAction( id: ID,
                              name: String,
                              connections: Seq[Coord]    = Seq.empty[Coord],
                              disconnections: Seq[Coord] = Seq.empty[Coord])
                              extends Action {
    override def description: String = super.description ++ s"A(n) \'$name\' with id \'${".."+ id.substring(29)}\' was "
    override def toString: String = description ++ connection_str
    def connection_str: String = if (verbose) s" Made connections to $connections and disconnected from $disconnections." else ""
  }

  case class AddAction(id: ID, name: String, to: Coord, conn: Seq[Coord])
                            extends PieceAction(id, name, connections = conn) {
    override def description: String = super.description ++ s"added at $to."
  }
  case class RemoveAction(id: ID, name: String, from: Coord, disconn: Seq[Coord])
                            extends PieceAction(id, name, disconnections = disconn) {
    override def description: String = super.description ++ s"removed at $from."
  }
  case class MoveAction(id: ID, name: String, from: Coord, to: Coord, conn: Seq[Coord], disconn: Seq[Coord])
                            extends PieceAction(id, name, conn, disconn) {
    override def description: String = super.description ++ s"moved from $from to $to."
  }
  case class RotateAction(id: ID, name: String, pos: Coord, rotate: Direction, conn: Seq[Coord], disconn: Seq[Coord])
                            extends PieceAction(id, name, conn, disconn) {
    override def description: String = super.description ++ s"rotated at $pos, and rotated ${rotate.n} units."
  }
  case class ToggleAction(id: ID, name: String, pos: Coord, a: Any)
                            extends PieceAction(id, name) {
    override def description: String = super.description ++ s"sent $a to $pos."
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












