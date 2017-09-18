package core.circuit

import core.types.ID.ID
import core.types.Signal.Signal
import core.types.{Direction, Side, Signal}
import core.circuit.Input

import scala.collection.mutable
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._

/**
  * Created by Mitch on 9/7/2017.
  */
class CircuitGraph {
  val sides:            mutable.Map[Direction, ID] = mutable.Map.empty[Direction, ID]
  val subcircuits:      mutable.Map[ID, Evaluable] = mutable.Map.empty[ID, Evaluable]
  val graph:            Graph[Side, DiEdge] = Graph.empty[Side, DiEdge]
  val dependency_graph: Graph[ID, DiEdge]   = Graph.empty[ID, DiEdge]

  def add(id: ID, eval: Evaluable): Unit = {
    subcircuits.put(id, eval)
    dependency_graph.add(id)
  }

  def remove(id: ID):               Option[Evaluable] = ???

  def connect(tuple: (Side, Side)): Boolean =
    if (subcircuits.contains(tuple._1.id) && subcircuits.contains(tuple._2.id)) {
      graph            += tuple._1    ~> tuple._2
      dependency_graph += tuple._1.id ~> tuple._2.id
      true
    } else false

  def disconnect(tuple: (Side, Side)): Boolean = ???

  def output_values: Array[Signal] = {
    val outs = Array.fill(4)(Signal.empty(0))
    for { dir <- Direction.values
          io  <- eval_on_side(dir)
          if io.isInstanceOf[Input]
        } outs(dir) = io.asInstanceOf[Input].values
    outs
  }

  def eval_on_side(dir: Direction): Option[Evaluable] = sides.get(dir).flatMap(subcircuits.get)

  def parents(id: ID):  Traversable[Side] =
    graph.nodes.filter(_.id == id).flatMap(_.diPredecessors).toOuterNodes

  def children(id: ID): Traversable[Side] =
    graph.nodes.filter(_.id == id).flatMap(_.diSuccessors).toOuterNodes

  def is_cyclic(): Boolean = graph.isCyclic

  def num_nodes: Int = this.subcircuits.size
  def num_edges: Int = this.graph.graphSize
}

















//