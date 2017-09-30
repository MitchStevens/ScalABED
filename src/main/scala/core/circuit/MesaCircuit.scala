package core.circuit

import core.circuit.Port.ConnectionType._
import core.types.ID.ID
import core.types._
import core.types.Signal._

import scala.collection.mutable
import scalax.collection.GraphEdge.DiEdge
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
    assert(!kv._2.eq(this), "You added a MesaCircuit to itself you dolt!")
    graphs += kv
    for (dir <- Direction.values)
      state += Side(kv._1, dir) -> Signal.empty(kv._2.num_inputs(dir))
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
  def connect(edge: (Side, Side)): Boolean = {
    val connection: Option[ConnectionType] = for{
      p1 <- graphs.subcircuit_port(edge._1)
      p2 <- graphs.subcircuit_port(edge._2)
      c_type <- Port.connection_type(p1, p2)
    } yield c_type
    connection match {
      case Some(TransmitsTo)  =>
        graphs.connect(edge)
        state.bind(edge)
        true
      case Some(ReceivesFrom) =>
        graphs.connect(edge.swap)
        state.bind(edge.swap)
        true
      case None => false
    }
  }

  def connect_all(tuples: (Side, Side)*): this.type = {
    tuples forall this.connect
    this
  }

  //TODO:
  def disconnect(side: Side): Boolean =
    if (graphs.disconnect(side)) {
      state.remove(side)
      true
    } else false

  def disconnect(id: ID): Boolean =
    if (graphs.disconnect(id)) {
      state.remove_state(id)
      true
    } else false

  /*
  * TODO: Error handling, this functijon could return a Either[EvalException, Array[Signal]] instead of Array[Signal]
  * */
  override def apply(ins: Array[Signal]): Array[Signal] = {
    //Handle this error, what if ins length != 4
    if (provide_inputs(ins))
      evaluate()
    recieved_outputs(state)
  }

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

    for (dir <- Direction.values)
      assert(this.num_outputs(dir) == outs(dir).length)

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
      val outputs = evaluable(inputs)
      state.set_state(id, outputs)
      //println(s"id: $id, \t"+ inputs.map(_.str).mkString(", ") +"\t ~~~> "+ outputs.map(_.str).mkString(", "))
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