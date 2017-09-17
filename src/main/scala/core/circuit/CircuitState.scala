package core.circuit

import core.types
import core.types.ID.ID
import core.types.{Direction, Side, Signal}
import core.types.Signal._

import scala.collection.mutable
import core.CatzInstances._

/**
  * Created by Mitch on 9/7/2017.
  *
  * A CircuitState is essentially a map from a Side to a Signal, with a few extra features:
  *   1. The ability to create dependancies between sides
  *
  *
  *
  */
class CircuitState extends Map[Side, Signal] {
  private val ports = mutable.HashMap.empty[Side, Either[Side, Signal]]


  /**
  * s1 is
  * */
  def dependant(parent: Side, child: Side): Unit = {
    ports += child -> Left(parent)
  }

  def unbind(s1: Side, s2: Side): Boolean = {
    for (p1 <- ports.get(s1); p2 <- ports.get(s2))
      return p1 disconnect p2
    false
  }

  def signal(side: Side): Option[Signal] = ports.get(side) match {
    case Some(Left(s))    => get_signal(s)
    case Some(Right(sig)) => Some(sig)
    case None             => None
  }

  def update(side: Side, signal: Signal): Unit = ports.get(side) match {
    case Some(Left(_))    => {} //The side is dependant on something else, don't set anything
    case _                => ports += side -> Right(signal)
  }

  def get_state(id: ID): Array[Signal] =
    for (dir <- Direction.values)
      yield signal(Side(id, dir)) getOrElse Signal.empty(0)

  def set_state(id: ID, signals: Array[Signal]): Unit =
  for {
    dir <- Direction.values
    if signals(dir).nonEmpty
  } this(Side(id, dir)) = signals(sir)

  override def toString: String = {
    val all_ids = ports.keysIterator.map(_.id).toSet[ID]
    val s = for {
      id <- all_ids
    } yield id.padTo(6, ' ') +" - "+ get_state(id).map(_.str).mkString(", ")
    "State: "+ this.hashCode() +"\nSides: "+ (if (s.isEmpty) "None" else  "\n"+ s.mkString("\n"))
  }
}
