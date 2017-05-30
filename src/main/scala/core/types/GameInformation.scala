package core.types

import core.circuit.{Evaluable}
import core.types.ID.ID

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 5/26/2017.
  */
class GameInformation {
  private val records: HashMap[ID, GameRecord] = HashMap.empty

  def apply(id: ID): Option[GameRecord] = records.get(id)

  def +=(kv: (ID, GameRecord)): Boolean = {
    val cond: Boolean = !(records.contains(kv._1) || records.exists(_._2.pos == kv._2.pos))

    if(cond)
      records += kv
    cond
  }

  def -=(id: ID): Option[GameRecord] =
    records.remove(id)

  def evaluable(id: ID): Option[Evaluable]  = records.get(id).map(_.evaluable)
  def id(pos: Coord): Option[ID]            = records.find(_._2.pos == pos).map(_._1)
  def pos(id: ID): Option[Coord]            = records.get(id).map(_.pos)
  def rot(id: ID): Option[Direction]        = records.get(id).map(_.rot)

  def update(id: ID, _pos: Coord): Unit =
    this(id) foreach (_.copy(pos = _pos))
  def update(id: ID, _rot: Direction): Unit =
    this(id) foreach (_.copy(rot = _rot))
}

case class GameRecord(pos: Coord, rot: Direction, evaluable: Evaluable)
