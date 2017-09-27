package core.circuit

import cats.Applicative
import cats.implicits._
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
class MesaCircuit extends mutable.Map[ID, Evaluable] with Evaluable {
  override val name: String = "MESACIRCUIT"

  var state:  CircuitState = new CircuitState()
  val graphs: CircuitGraph = new CircuitGraph()

  override def get(key: ID): Option[Evaluable] =     graphs.get(key)
  override def iterator: Iterator[(ID, Evaluable)] = graphs.iterator

  override def +=(kv: (ID, Evaluable)): MesaCircuit.this.type = {
    graphs += kv
    val s = for (dir <- Direction.values)
      yield Side(kv._1, dir) -> Signal.empty(kv._2.num_inputs(dir))
    state ++= s
    this
  }

  override def -=(key: ID) :MesaCircuit.this.type = {
    graphs -= key
    state --= Direction.values map (d => Side(key, d))
    this
  }

  override def size: Int = graphs.size

  /** connect side a to side b
    *
  * */
  def connect(edge: (Side, Side)): Boolean =
    if (potential_connection(edge)) {
      println(s"mesa: connected two sides: $edge")
      graphs.connect(edge)
      state.bind(edge)
      true
    } else false

  def connect_all(tuples: (Side, Side)*): this.type = {
    tuples forall this.connect
    this
  }

  /**
    * if ()
    *
    * */
  def disconnect(side: Side): Boolean = ???

  def disconnect(id: ID): Boolean =
    if (true) {
      graphs.disconnect(id)
      state.remove_state(id)
      true
    } else false

  private def potential_connection(edge: (Side, Side)): Boolean = {
    val parent = graphs.subcircuit_port(edge._1)
    val child  = graphs.subcircuit_port(edge._2)
    Applicative[Option].map2(parent, child)(Port.connection_precondition) getOrElse false
  }

  /*
  * TODO: Error handling, this functijon could return a Either[EvalException, Array[Signal]] instead of Array[Signal]
  * */
  override def apply(ins: Array[Signal]): Array[Signal] = {
    //Handle this error, what if ins length != 4
    if (provide_inputs(ins))
      evaluate()
    recieved_outputs(state)
  }

  //
  private def provide_inputs(ins: Array[Signal]): Boolean = {
    for (dir <- Direction.values)
      assert(this.num_inputs(dir) == ins(dir).length)

    for {
      dir      <- Direction.values
      circuit <- graphs.eval_on_side(dir)
    } circuit match {
      case in: Input => in set ins(dir)
      case _         => {}
    }
    true
  }

  def evaluate(): Unit = { state = next_state(state) }

  def recieved_outputs(state: CircuitState): Array[Signal] = {
    val outs: Array[Signal] = Array.fill(4)(Signal.empty(0))
    for {
      dir <- Direction.values
      id  <- graphs.side(dir)
      sig <- state.get(Side(id, Direction.LEFT))
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

  def set_side(dir: Direction, id: ID): Unit =
    graphs.set_side(dir, id)

  def set_sides(sides: (Direction, ID)*): this.type = {
    sides foreach (t => this.set_side(t._1, t._2))
    this
  }

  // Should this be pure? Could pass in the CircuitGraph?
  def next_state(state: CircuitState): CircuitState = {
    val toposort_data = MesaCircuit.pseudo_toposort(graphs.dependancies())
    for(id <- toposort_data.toposorted) {
      val evaluable = graphs(id)
      val inputs: Array[Signal] = state.get_state(id)
      val outputs = evaluable.apply(inputs)
      state.set_state(id, outputs)
      println(s"id: $id, \t"+ inputs.map(_.str).mkString(", ") +"\t ~~~> "+ outputs.map(_.str).mkString(", "))
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