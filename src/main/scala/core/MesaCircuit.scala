package core

import core.types.Direction

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 3/20/2017.
  */
trait MesaCircuit extends Circuit {
  import MesaCircuit._

  val side_circuits: Array[Option[Evaluable]] = Array.fill(4)(None)

  val nodes: HashMap[ID, Evaluable]
  val edges: HashMap[Edge, Edge]

  def add(kv: (ID, Evaluable)): Boolean ={
    val cond = !(nodes contains kv._1)
    if (cond)
      nodes += kv
    cond
  }

  def add_all(kvs: List[(ID, Evaluable)]): Boolean = {
    val was_added: List[Boolean] = kvs map add
    was_added.fold(true)(_ && _)
  }

  def remove(id: ID): Option[Evaluable]

  def connect(from: Edge, to: Edge): Boolean
  def disconnect(from: Edge, to: Edge): Boolean

  //def evaluate(id: ID): Boolean
}

object MesaCircuit {
  type ID = String
  type Edge = (ID, Direction)
}