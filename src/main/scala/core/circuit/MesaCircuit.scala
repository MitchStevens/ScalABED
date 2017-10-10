package core.circuit

import core.circuit.Port.ConnectionType._
import core.circuit.Port.PortType
import core.types.ID.ID
import core.types._
import core.types.Signal._

import scala.collection.mutable._
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.mutable.Graph

/**
  * Created by Mitch on 4/23/2017.
  */
  class MesaCircuit extends Map[ID, Evaluable] with Evaluable {
  override val name: String = "MESACIRCUIT"

  var state:  CircuitState = new CircuitState()
  val graphs: CircuitGraph = new CircuitGraph()
  private val sides: Map[Direction, ID] = Map.empty[Direction, ID]

  override def get(key: ID): Option[Evaluable] =     graphs.get(key)
  override def iterator: Iterator[(ID, Evaluable)] = graphs.iterator

  override def +=(kv: (ID, Evaluable)): MesaCircuit.this.type = {
    assert(!kv._2.eq(this), "You added a MesaCircuit to itself you dolt!")
    graphs += kv
    for (dir <- Direction.values) {
      val outs = kv._2.num_outputs(dir)
      if (outs > 0)
        state += Side(kv._1, dir) -> Signal.empty(outs)
    }
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

  def disconnect(edge: (Side, Side)): Boolean = {
    val connection: Option[ConnectionType] = for{
      p1 <- graphs.subcircuit_port(edge._1)
      p2 <- graphs.subcircuit_port(edge._2)
      c_type <- Port.connection_type(p1, p2)
    } yield c_type
    connection match {
      case Some(TransmitsTo)  =>
        graphs.disconnect(edge)
        state.unbind(edge._2)
        true
      case Some(ReceivesFrom) =>
        graphs.disconnect(edge.swap)
        state.unbind(edge._1)
        true
      case None => false
    }
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

  private def provide_inputs(ins: Array[Signal]): Boolean = {
    for (dir <- Direction.values)
      assert(this.num_inputs(dir) == ins(dir).length)

    for {
      dir     <- Direction.values
      circuit <- sides.get(dir) flatMap graphs.get
    } circuit match {
      case in: Input => in set ins(dir)
      case _         => {}
    }
    true
  }

  def evaluate(): Unit = { state = next_state(state) }

  def recieved_outputs(state: CircuitState): Array[Signal] = {
    def get_output(dir: Direction): Signal = {
      val out: Option[Signal] = for {
        id <- sides.get(dir)
        e  <- graphs.get(id)
        if e.isInstanceOf[Output]
        sig <- state.get(Side(id, Direction.LEFT))
      } yield sig
      out
    } getOrElse Signal.empty(0)

    Direction.values map get_output
  }

  override def num_inputs(dir: Direction): Int =
    this.side_port(dir) match {
      case Port(PortType.IN, size) => size
      case _                       => 0
    }

  override def num_outputs(dir: Direction): Int =
    this.side_port(dir) match {
      case Port(PortType.OUT, size) => size
      case _                        => 0
    }

  private def side_port(dir: Direction): Port = {
    sides.get(dir) flatMap this.get map {
      case in: Input   => Port.in(in.capacity)
      case out: Output => Port.out(out.capacity)
      case _           => Port.unused
    }
  } getOrElse Port.unused

  def set_side(dir: Direction, id: ID): Unit =
    sides += dir -> id

  def remove_side(dir: Direction): Unit =
    sides -= dir

  // Should this be pure? Could pass in the CircuitGraph?
  def next_state(state: CircuitState): CircuitState = {
    val toposort_data: ToposortData[ID] = graphs.pseudo_toposort()
    for(id <- toposort_data.toposorted) {
      val evaluable = graphs(id)
      val inputs: Array[Signal] = state.get_state_padded(id, evaluable.num_input_array)
      val outputs = evaluable(inputs)
      state.set_state(id, outputs)
      //println(s"id: $id, \t"+ inputs.map(_.str).mkString(", ") +"\t ~~~> "+ outputs.map(_.str).mkString(", "))
    }
    state
  }

}









//