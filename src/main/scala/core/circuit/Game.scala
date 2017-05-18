package core.circuit

import core.types.Edge.Edge
import core.types.ID.ID

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 4/23/2017.
  */
class Game {

  val edges: HashMap[Edge, Edge] = ???
  val nodes: HashMap[ID, Evaluable] = ???

  def connect(to: Edge, from: Edge): Boolean = ???
  def disconnect(to: Edge, from: Edge): Boolean = ???
  def remove(id: ID): Option[Evaluable] = ???

}

object Game {

}
