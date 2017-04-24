package core

import akka.actor.ActorRef
import akka.pattern.ask
import core.MesaCircuit.{Edge, ID}
import core.Port._
import core.types.Signal
import core.types.Signal.Signal
import core.ConcurrencyContext._

import scala.collection.mutable.{HashMap, Queue}
import scala.concurrent.{Await, Future}

/**
  * Created by Mitch on 4/23/2017.
  */
class Function extends MesaCircuit with Evaluable {
  val evaluation_list: Queue[ID] = Queue.empty
  val sides: Array[Option[ID]] = Array.fill(4)(None)

  val edges: HashMap[Edge, Edge] = HashMap.empty
  val nodes: HashMap[ID, Evaluable] = HashMap.empty

  val last_inputs:  Array[Signal] = Array.fill(4)(Signal.empty(0))
  val last_outputs: Array[Signal] = Array.fill(4)(Signal.empty(0))
  val num_inputs:  Array[Int] = Array.fill(4)(0)
  val num_outputs: Array[Int] = Array.fill(4)(0)
  val ports: Array[ActorRef] = Array.fill(4)(Port.inst(PortType.UNUSED, 0))

  override def request_inputs() {

  }

  override def send_outputs() {

  }

  def calc_outputs() {

  }

  def set_side() {

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
  def connect(from: Edge, to: Edge): Boolean = ???/*{
    val connection_attempt: Option[Future[Any]] =
      for {
        port_out <- get_port(from)
        port_in  <- get_port(to)
        future   <- port_out ? ConnectTo(port_in)
      } yield future
    val connection_status = connection_attempt.flatMap(Await.result(_, future_wait))
    connection_status
  }*/

  def disconnect(from: Edge, to: Edge): Boolean = ???/*{
    val future_connection =
      for {
        port_out <- get_port(from)
        port_in  <- get_port(to)
      } yield port_out ? DisconnectFrom(port_out)
  }*/

  def remove(id: ID): Option[Evaluable] = ??? /*{
    null
  }
*/
  def get_port(edge: Edge): Option[ActorRef] = {
    val circuit: Option[Evaluable] = nodes get edge._1
    circuit.map(_.ports.apply(edge._2))
  }

}
