package core

import core.implicits.Direction

import scala.collection.mutable.HashMap

/**
  * Created by Mitch on 3/20/2017.
  */
trait MetaCircuit extends Circuit {
  import MetaCircuit._
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

  def connect(to: Edge, from: Edge): Boolean
  def disconnect(to: Edge, from: Edge): Boolean

  //def evaluate(id: ID): Boolean
}

object MetaCircuit {
  type ID = String
  type Edge = (ID, Direction)
}