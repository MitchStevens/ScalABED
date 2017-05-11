package core

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import core.ConcurrencyContext._
import core.Port._
import core.Port.PortType.PortType
import core.types.Signal
import core.types.Signal.Signal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
/**
  * Created by Mitch on 4/11/2017.
  */
class Port(port_type: PortType, capacity: Int) extends Actor {
  var signal: Signal = Signal.empty(capacity)
  var spouse: Option[ActorRef] = None

  def connect_to(port: ActorRef): Boolean = {
    if (self eq port) return false

    val cond = Await.result(connection_precondition(port), future_wait)
    if (cond){
      this.spouse = Some(port)
      port ! SetSpouse(self)
    }
    cond
  }

  def connection_precondition(port: ActorRef): Future[Boolean] = {
    def capacity_condition(other_capacity: Any): Boolean = {this.capacity == other_capacity}
    def port_type_condition(other_type: Any): Boolean = this.port_type match {
      case PortType.IN  => false
      case PortType.OUT => other_type == PortType.IN
      case PortType.UNUSED => false
    }

    val future_capacity = port ? Capacity
    val future_type     = port ? PortType
    for {
      other_capacity <- future_capacity
      other_type <- future_type
    } yield capacity_condition(other_capacity) && port_type_condition(other_type)
  }

  def disconnect_from(port: ActorRef): Boolean = {
    if (self eq port) return false

    val has_connection: Boolean =
      spouse.exists(port.eq)
    if (has_connection){
      port ! RemoveSpouse
      this.remove_spouse()
      set_input(Signal.empty(capacity))
    }
    has_connection
  }

  def get_spouse: Option[ActorRef] = this.spouse
  def set_spouse(spouse: ActorRef) = {this.spouse = Some(spouse)}
  def remove_spouse() {
    this.spouse = None
    if (is_input)
      signal = Signal.empty(capacity)
  }

  override def receive = {
    case Capacity => sender ! capacity
    case PortType => sender ! port_type

    case GetInput => sender ! get_input
    case SetInput(signal) => set_input(signal)
    case GetOutput => sender ! get_output
    case SetOutput(signal) => set_output(signal)

    case ConnectTo(port) => sender ! connect_to(port)
    case DisconnectFrom(port) => sender ! disconnect_from(port)
    case GetSpouse => sender ! get_spouse
    case SetSpouse(spouse) => set_spouse(spouse)
    case RemoveSpouse => remove_spouse()
  }

  def get_input: Signal = {
    if (is_input) signal
    else Signal.empty(0)
  }

  def set_input(signal: Signal) = {
    if(is_input)
      this.signal = signal
  }

  def get_output: Signal =
    if (is_output) signal
    else Signal.empty(0)

  def set_output(signal: Signal) = {
    spouse foreach (_ ! SetInput(signal))
    if (is_output)
      this.signal = signal
  }

  def is_input:  Boolean = {port_type == PortType.IN}
  def is_output: Boolean = {port_type == PortType.OUT}

  override def toString: String = s"Port: $port_type -> $capacity"
}

object Port {

  object PortType extends Enumeration {
    type PortType = Value
    val IN, OUT, UNUSED = Value
  }

  case object Capacity

  case object GetInput
  case class  SetInput(signal: Signal)
  case object GetOutput
  case class  SetOutput(signal: Signal)

  case class  ConnectTo(spouse: ActorRef)
  case class  DisconnectFrom(spouse: ActorRef)
  case object GetSpouse
  case class  SetSpouse(spouse: ActorRef)
  case object RemoveSpouse

  def inst(port_type: PortType, capacity: Int): ActorRef = {
    val props = Props(classOf[Port], port_type, capacity)
    actor_system.actorOf(props)
  }

  def create(ins: Int, outs: Int): ActorRef =
    if (ins >  0 && outs == 0)
      Port.inst(PortType.IN, ins)
    else if (ins == 0 && outs > 0)
      Port.inst(PortType.OUT, outs)
    else if (ins == 0 && outs == 0)
      Port.inst(PortType.UNUSED, 0)
    else throw new Error(s"Couldn't create a port with $ins inputs and $outs outputs.")
}
