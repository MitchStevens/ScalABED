package core.circuit

import core.types.ID.ID
import core.types._
import core.types.Side.SideMethods
import core.types.Side.Side

import scala.collection.mutable._
import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import scalax.collection.edge.LBase.LEdge
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

  def connect(conn: (Side, Side)): Boolean =
    if (this.contains(conn._1.id) && this.contains(conn._2.id)) {
      graph.add(new Edge[ID](conn))
      true
    } else false

  def disconnect(conn: (Side, Side)): Boolean = {
    for {
      edge: graph.EdgeT <- graph.find(new Edge(conn))
    } yield graph.remove(edge.toOuter)
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
        for (child <- id.diPredecessors)
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

class Edge[A](s1: (A, Direction), s2: (A, Direction))
  extends DiEdge[A](s1._1, s2._1)
  with    LEdge[A] {

  def this(t: ((A, Direction), (A, Direction))) =
    this(t._1, t._2)

  override type L1 = (Direction, Direction)
  override def label: L1 = (s1._2, s2._2)
}

case class ToposortData[A](toposorted: ArrayBuffer[A], critical: Set[A])








//