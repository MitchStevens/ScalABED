package core.circuit

import core.circuit.Port.PortType
import core.types.Edge.Edge
import core.types.ID.ID
import core.types.{Direction, Signal}
import core.types.Signal.Signal

import scala.collection.mutable.{HashMap, Queue}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Mitch on 4/23/2017.
  */
class Function extends Evaluable {
  val evaluation_list: Queue[ID] = Queue.empty
  val sides:      Array[Option[IOCircuit]] = Array.fill(4)(None)
  val side_names: Array[ID]                = Array.fill(4)("")

  val edges: HashMap[Edge, Edge]    = HashMap.empty
  val nodes: HashMap[ID, Evaluable] = HashMap.empty

  val last_inputs:  Array[Signal] = Array.fill(4)(Signal.empty(0))
  val last_outputs: Array[Signal] = Array.fill(4)(Signal.empty(0))
  val num_inputs:   Array[Int]    = Array.fill(4)(0)
  val num_outputs:  Array[Int]    = Array.fill(4)(0)
  val ports:        Array[Port]   = Array.fill(4)(new Port(PortType.UNUSED, 0))

  val eval_loop: Future[Unit] =
    Future {
      while(true) {
        //println(s"evls: $evaluation_list")
        if (evaluation_list.nonEmpty) {
          val id: ID = evaluation_list.dequeue()
          nodes(id).evaluate()
          evaluation_list ++= adj(id)
        }
        Thread.sleep(2)
      }
    }

  def size: Int = nodes.size

  /*
  * 1. Gets the input values from the ports.
  * 2. Transmits this to all the sides that are also inputs
  *   2a. Set the inputs
  *   2b. Add these inputs to the evaluation queue
  * */
  override def request_inputs(): Unit = {
    for (i <- 0 to 3){
      last_inputs(i) = ports(i).get_input
      if (sides(i).isDefined){
        sides(i).get.port set_input last_inputs(i)
        evaluation_list += side_names(i)
      }
    }
  }

  /*
  * Function is constantly evaluating, does nothing
  * */
  override def calc_outputs(): Unit = {
  }

  /*
  * First get the information out of the sides that are outputs, then transmit this information to the ports.
  * */
  override def send_outputs(): Unit = {
    for (i <- 0 to 3)
      if (sides(i).isDefined) {
        last_outputs(i) = sides(i).get.port.get_output
        ports(i) set_output last_outputs(i)
      }
  }

  def set_side(dir: Direction, id: ID) {
    def side_pred(evaluable: Evaluable): Boolean =
      evaluable.isInstanceOf[IOCircuit] && !sides.contains(Some(evaluable))

    val cond: Boolean = nodes.get(id) exists side_pred

    if (cond) {
      nodes(id) match {
        case input: Input  =>
          this.num_inputs(dir)  = input.capacity
          this.ports(dir) = new Port(PortType.IN, input.capacity)
        case output: Output =>
          this.num_outputs(dir) = output.capacity
          this.ports(dir) = new Port(PortType.OUT, output.capacity)
      }
      this.sides(dir)      = Some(nodes(id).asInstanceOf[IOCircuit])
      this.side_names(dir) = id
    }

  }

  def remove_side(dir: Direction): Unit = {
    this.sides(dir)         = None
    this.side_names(dir)    = ""
    this.num_inputs(dir)    = 0
    this.num_outputs(dir)   = 0
    this.last_inputs(dir)   = Signal.empty(0)
    this.last_outputs(dir)  = Signal.empty(0)
    this.ports(dir)         = new Port(PortType.UNUSED, 0)
  }

  /** Given two edges, make an attempt to connect them
    *
    * @param  from: An Edge representing an output port
    * @param  to:   An Edge representing an input port
    * @return true if the connection was successfully created, false if:
    * <li>
    *   <ul>The params `from` and `to` do not represent any Port in the function</ul>
    *   <ul>The connection criteria {@see Port} are not met</ul>
    * </li>
    * */
  def connect(from: Edge, to: Edge): Boolean = {
    val connection_status: Option[Boolean] =
      for {
        port_out <- get_port(from)
        port_in  <- get_port(to)
      } yield port_out connect_to port_in

    val cond = connection_status.getOrElse(false)
    if(cond){
      edges += (from -> to)
      evaluation_list += to.id
    }
    return cond
  }

  def disconnect(from: Edge, to: Edge): Boolean = {
    val disconnection_status: Option[Boolean] =
      for {
        port_out <- get_port(from)
        port_in  <- get_port(to)
      } yield port_out disconnect_from port_in
    val cond = disconnection_status.getOrElse(false)
    if(cond)
      evaluation_list += to.id
    return cond
  }

  def add(kv: (ID, Evaluable)): Boolean ={
    val cond = !(nodes contains kv._1)
    if (cond)
      nodes += kv
    cond
  }

  def remove(id: ID): Option[Evaluable] =
    nodes.remove(id)

  protected def get_port(edge: Edge): Option[Port] = {
    val circuit: Option[Evaluable] = nodes get edge.id
    circuit.map(_.ports(edge.dir))
  }

  protected def adj(id: ID): Iterable[ID] = {
    edges.filterKeys(_.id == id).values.map(_.id)
  }

}
