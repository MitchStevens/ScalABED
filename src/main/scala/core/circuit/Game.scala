package core.circuit

import core.circuit.MesaCircuit.{Edge, ID}

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 4/23/2017.
  */
class Game extends MesaCircuit {

  def connect(to: Edge, from: Edge): Boolean = ???
  def disconnect(to: Edge, from: Edge): Boolean = ???
  val edges: HashMap[Edge, Edge] = ???
  val nodes: HashMap[ID, Evaluable] = ???
  def remove(id: ID): Option[Evaluable] = ???

}

object Game {

}
