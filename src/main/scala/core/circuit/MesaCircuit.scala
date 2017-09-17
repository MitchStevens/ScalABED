package core.circuit

import core.types.ID.ID
import core.types.{Side, _}
import core.types.Signal._

import scala.collection.mutable
import scala.collection.mutable.{HashMap, Queue}
import scalax.collection.GraphEdge.{DiEdge, DiEdgeLike}
import scalax.collection.mutable.Graph

/**
  * Created by Mitch on 4/23/2017.
  */
class MesaCircuit extends Evaluable {
  var state:  CircuitState = new CircuitState()
  val graphs: CircuitGraph = new CircuitGraph()

  def add(id: ID, evaluable: Evaluable): Unit = {
    graphs.add(id, evaluable)
    state = next_state(state)
  }

  def connect(edge: (Side, Side)): Unit = {
    graphs.connect(edge)
    state.bind(edge._1, edge._2)
    state = next_state(state)
  }

  /*
  * TODO: Error handling, this functijon could return a Either[EvalException, Array[Signal]] instead of Array[Signal]
  * */
  override def apply(ins: Array[Signal]): Array[Signal] = {
    //Handle this error, what if ins length != 4
    for {
      dir  <- Direction.values
      edge <- graphs.side(dir)
      if edge.isInstanceOf[Input]
    } edge.asInstanceOf[Input] set ins(dir) //Handle this
    state = next_state(state)
    this.graphs.output_values
  }

  override def num_inputs(dir: Direction): Int =
    graphs.side(dir) map {
      case in: Input => in.capacity
      case _         => 0
    } getOrElse 0

  override def num_outputs(dir: Direction): Int =
    graphs.side(dir) map {
      case out: Output => out.capacity
      case _           => 0
    } getOrElse 0

  def size: Int = graphs.subcircuits.size

  def set_side(dir: Direction, id: ID): Unit = {
    graphs.sides(dir) = id
  }

  // Should this be pure? Could pass in the CircuitGraph?
  def next_state(state: CircuitState): CircuitState = {
    //evaluate inputs
    for {
      dir  <- Direction.values
      edge <- graphs.side(dir)
      if edge.isInstanceOf[Input]
    } {
      val s = edge.asInstanceOf[Input].apply(Array.fill(4)(Signal.empty(0)))
      state.set_state(graphs.sides(dir), s)
    }

    val toposort_data = MesaCircuit.pseudo_toposort(graphs.dependency_graph)
    for(id <- toposort_data.toposorted) {
      val inputs: Array[Signal] = state.get_state(id)
      val outputs = graphs.subcircuits(id).apply(inputs)
      println(s"id: $id, \t"+ inputs.map(_.str).mkString(", ") +"\t ~~~> "+ outputs.map(_.str).mkString(", "))
      state.set_state(id, outputs)
    }
    state
  }

}

object MesaCircuit {
  /**
    * This function is based on the depth-first search algorithm. It returns two pieces of
    * information:
    *   A sorted list `toposorted`, ordered by dependency in the graph
    *   A list of critical nodes
    *
    * */
  def pseudo_toposort[A](graph: Graph[A, DiEdge]): ToposortData[A] = {
    var toposorted = List.empty[A]
    var critical   = Set.empty[A]
    var unmarked   = graph.nodes.toSet
    val marked     = mutable.HashMap.empty[A, Mark]
    trait Mark
    case object Temporary extends Mark
    case object Permanent extends Mark

    def visit(id: graph.NodeT): Unit = marked.get(id) match {
      case Some(Permanent) => {}
      case Some(Temporary) => critical = critical + id.value
      case None            => {
        unmarked -= id
        marked(id) = Temporary
        for (child <- id.diSuccessors)
          visit(child)
        marked(id) = Permanent
        toposorted = id.value :: toposorted
      }
    }

    while(unmarked.nonEmpty)
      visit(unmarked.head)
    ToposortData(toposorted, critical)
  }

  case class ToposortData[A](toposorted: List[A], critical: Set[A])
}









//