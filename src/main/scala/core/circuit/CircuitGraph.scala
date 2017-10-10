package core.circuit

import core.types.ID.ID
import core.types.Signal.Signal
import core.types._
import core.circuit.Edge

import scala.collection.mutable._
import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._, scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LDiEdge

/**
  * Created by Mitch on 9/7/2017.
  *
  * A CircuitGraph is the set of all the graphs needed to accurately model all the evaluables and their relationships
  * to one another.
  */
class CircuitGraph extends Map[ID, Evaluable] {
  private val subcircuits: Map[ID, Evaluable] = Map.empty[ID, Evaluable]
  private val graph:       Graph[ID, Edge]    = Graph.empty[ID, Edge]

  override def get(key: ID): Option[Evaluable] = subcircuits.get(key)
  override def iterator: Iterator[(ID, Evaluable)] = subcircuits.iterator

  override def +=(kv: (ID, Evaluable)): CircuitGraph.this.type = {
    subcircuits += kv
    graph += kv._1
    //assert(subcircuits.size == dependency_graph.size)
    this
  }

  override def -=(key: ID): CircuitGraph.this.type = {
    subcircuits -= key
    graph -= key
    this
  }

  def connect(tuple: (Side, Side)): Boolean =
    if (this.contains(tuple._1.id) && this.contains(tuple._2.id)) {
      graph.add(new Edge(tuple))
      true
    } else false

  def disconnect(conn: (Side, Side)): Boolean = {
    for {
      edge: graph.EdgeT <- graph.find(new Edge(conn))
      if edge.label == (conn._1.dir, conn._2.dir)
    } yield graph.remove(edge.asInstanceOf[Edge[ID]])
  } getOrElse false

  def subcircuit_port(side: Side): Option[Port] = this.get(side.id).map(_.ports(side.dir))

  def is_cyclic(): Boolean = graph.isCyclic

  def num_nodes: Int = graph.size
  def num_edges: Int = graph.edges.length

  def pseudo_toposort(): ToposortData[ID] = CircuitGraph.pseudo_toposort(graph)
}

object CircuitGraph {
  /**
    * This function is based on the depth-first search algorithm. It returns two pieces of
    * information:
    *   A sorted list `toposorted`, ordered by dependency in the graph
    *   A list of critical nodes
    *
    * */
  protected def pseudo_toposort[A](graph: Graph[A, Edge]): ToposortData[A] = {
    var toposorted = ArrayBuffer.empty[A]
    var critical   = Set.empty[A]
    var unmarked   = graph.nodes.toSet
    val marked     = HashMap.empty[A, Mark]
    sealed trait Mark
      case object Temporary extends Mark
      case object Permanent extends Mark

    def visit(id: graph.NodeT): Unit = marked.get(id) match {
      case Some(Permanent) => {}
      case Some(Temporary) => critical += id.value
      case None            => {
        unmarked -= id
        marked(id) = Temporary
        for (child <- id.diSuccessors)
          visit(child)
        marked(id) = Permanent
        toposorted += id.value
      }
    }

    while (unmarked.nonEmpty)
      visit(unmarked.head)
    ToposortData(toposorted, critical)
  }
}

class Edge[+A](s1: Side, s2: Side) extends LDiEdge[A](s1.id, s2.id) {

  def this(tuple: (Side, Side)) = this(tuple._1, tuple._2)

  override type L1 = (Direction, Direction)
  override def label: (Direction, Direction) = (s1.dir, s2.dir)
}

case class ToposortData[A](toposorted: ArrayBuffer[A], critical: Set[A])








//