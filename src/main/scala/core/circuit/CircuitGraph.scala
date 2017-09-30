package core.circuit

import core.types.ID.ID
import core.types.Signal.Signal
import core.types._

import scala.collection.mutable
import scalax.collection.GraphBase._
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.mutable.{Graph, GraphLike}
import scalax.collection.GraphPredef._

/**
  * Created by Mitch on 9/7/2017.
  *
  * A CircuitGraph is the set of all the graphs needed to accurately model all the evaluables and their relationships
  * to one another.
  */
class CircuitGraph extends mutable.Map[ID, Evaluable]{
  private val subcircuits:      mutable.Map[ID, Evaluable] = mutable.Map.empty[ID, Evaluable]
  private val sides:            mutable.Map[Direction, ID] = mutable.Map.empty[Direction, ID]
  private val connections:      ConnectedList[Side] = new ConnectedList[Side]()
  private val dependency_graph: Graph[ID, DiEdge]   = Graph.empty[ID, DiEdge]

  def side(dir: Direction): Option[ID] =       sides.get(dir)
  def rem_side(dir: Direction): Unit =         sides -= dir
  def set_side(dir: Direction, id: ID): Unit = sides += dir -> id

  def dependancies(): Graph[ID, DiEdge] =      dependency_graph

  override def get(key: ID): Option[Evaluable] = subcircuits.get(key)
  override def iterator: Iterator[(ID, Evaluable)] = subcircuits.iterator

  override def +=(kv: (ID, Evaluable)): CircuitGraph.this.type = {
    subcircuits += kv
    dependency_graph += kv._1
    //assert(subcircuits.size == dependency_graph.size)
    this
  }

  override def -=(key: ID): CircuitGraph.this.type = {
    subcircuits -= key
    dependency_graph -=key
    disconnect(key)
    for (dir <- is_side(key))
      rem_side(dir)
    this
  }

  //Is the side connected to some other side?
  def is_connected(side: Side): Option[Side] = {
    connections.get(side).flatMap(_.adjacent)
  }

    def connect(tuple: (Side, Side)): Boolean =
    if (this.contains(tuple._1.id) && this.contains(tuple._2.id)) {
      connections      += tuple._1    -> tuple._2
      dependency_graph += tuple._1.id ~> tuple._2.id
      true
    } else false

  /**
    *
    */
  def disconnect(side: Side): Boolean = connections.get(side) match {
    case Some(Parent(_, c)) =>
      connections      -= side
      dependency_graph -= side.id ~> c.value.id
      true
    case Some(Child(_, p)) =>
      connections      -= side
      dependency_graph -= p.value.id ~> side.id
      true
    case None => println("\t"+ side); false
  }

  def disconnect(id: ID): Boolean = {
    dependency_graph.remove(id)
    for (dir <- Direction.values)
      connections -= Side(id, dir)
    true
  }

  def is_side(id: ID): Option[Direction] =
      Direction.values.find(side(_).exists(_ == id))

  def output_values: Array[Signal] = {
    val outs = Array.fill(4)(Signal.empty(0))
    for { dir <- Direction.values
          io  <- eval_on_side(dir)
          if io.isInstanceOf[Input]
        } outs(dir) = io.asInstanceOf[Input].values
    outs
  }

  def eval_on_side(dir: Direction): Option[Evaluable] = sides.get(dir).flatMap(this.get)

  def subcircuit_port(side: Side): Option[Port] = this.get(side.id).map(_.ports(side.dir))

  def inputting_to(id: ID):  Traversable[Side] =
    for {
      dir    <- Direction.values
      node   <- connections.get(Side(id, dir))
      parent <- node.parent
    } yield parent


  def outputting_to(id: ID): Traversable[Side] =
    for {
      dir    <- Direction.values
      node   <- connections.get(Side(id, dir))
      child <- node.child
    } yield child

  def is_cyclic(): Boolean = dependency_graph.isCyclic

  def num_nodes: Int = dependency_graph.size
  def num_edges: Int = connections.size
}

















//