package core.circuit

import core.types
import core.types.ID.ID
import core.types.{BindMap, Direction, Side, Signal}
import core.types.Signal._

import core.types.Side.Side

/**
  * Created by Mitch on 9/7/2017.
  *
  * A CircuitState is essentially a map from a Side to a Signal, with the ability to create dependancies between sides.
  * This functionality is implemented by a class called BindMap, which is an extention of a mutable Map.
  */
class CircuitState extends BindMap[Side, Signal] {

  def get_state(id: ID): Array[Signal] =
    for (dir <- Direction.values)
      yield this.getOrElse((id, dir), Signal.empty(0))

  def get_state_padded(id: ID, size: Array[Int]): Array[Signal] =
    for (dir <- Direction.values)
      yield this.getOrElse((id, dir), Signal.empty(size(dir)))

  def set_state(id: ID, signals: Array[Signal]): Unit =
    for {
      dir <- Direction.values
      if signals(dir).nonEmpty
    } this += (id, dir) -> signals(dir)

  def remove_state(id: ID): Unit =
    for (dir <- Direction.values)
      this -= Side(id, dir)

  override def toString: String = {
    val all_ids = this.keysIterator.map(_._1).toSet[ID]
    val s = for {
      id <- all_ids
    } yield id.padTo(6, ' ') +" - "+ get_state(id).map(_.str).mkString(", ")
    "State: "+ this.hashCode() +"\nSides: "+ (if (s.isEmpty) "None" else  "\n"+ s.mkString("\n"))
  }
}
