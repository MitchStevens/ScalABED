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
    state.set_state(id, evaluable.num_input_array map Signal.empty)
  }

  def add_all(tuples: List[(ID, Evaluable)]): Unit =
    tuples foreach ((t: (ID, Evaluable)) => this.add(t._1, t._2))

  /** connect side a to side b
    *
  * */
  def connect(edge: (Side, Side)): Unit = {
    graphs.connect(edge)
    state.dependant(edge)
    state = next_state(state)
  }

  /*
  * TODO: Error handling, this functijon could return a Either[EvalException, Array[Signal]] instead of Array[Signal]
  * */
  override def apply(ins: Array[Signal]): Array[Signal] = {
    //Handle this error, what if ins length != 4
    if (provide_inputs(ins: Array[Signal]))
      state = next_state(state)
    recieved_outputs(state)
  }


  //
  private def provide_inputs(ins: Array[Signal]): Boolean = {
    for {
      dir      <- Direction.values
      circuit <- graphs.eval_on_side(dir)
    } circuit match {
      case in: Input => in set ins(dir) //Handle this
      case _         => {}
    }
    true
  }

  def recieved_outputs(state: CircuitState): Array[Signal] = {
    val outs: Array[Signal] = Array.fill(4)(Signal.empty(0))
    for {
      dir <- Direction.values
      id  <- graphs.sides.get(dir)
      sig <- state(Side(id, Direction.LEFT))
      if graphs.eval_on_side(dir).isInstanceOf[Option[Output]]
    } outs(dir) = sig
    outs
  }

  override def num_inputs(dir: Direction): Int =
    graphs.eval_on_side(dir) map {
      case in: Input => in.capacity
      case _         => 0
    } getOrElse 0

  override def num_outputs(dir: Direction): Int =
    graphs.eval_on_side(dir) map {
      case out: Output => out.capacity
      case _           => 0
    } getOrElse 0

  def size: Int = graphs.subcircuits.size

  def set_side(dir: Direction, id: ID): Unit = {

    graphs.sides(dir) = id
  }

  // Should this be pure? Could pass in the CircuitGraph?
  def next_state(state: CircuitState): CircuitState = {
    val toposort_data = MesaCircuit.pseudo_toposort(graphs.dependency_graph)
    for(id <- toposort_data.toposorted) {
      //print(s"id: $id, \t")
      val evaluable = graphs.subcircuits(id)
      val inputs: Array[Signal] = state.get_state(id)
      //print(inputs.map(_.str).mkString(", ") +"\t ~~~> ")
      val outputs = evaluable.apply(inputs)
      //println(outputs.map(_.str).mkString(", "))
      state.set_state(id, outputs)
      //println(state.toString)
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