package core

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import core.ConcurrencyContext._
import core.Port._
import core.Port.PortType.PortType
import core.Signal.Signal

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
/**
  * Created by Mitch on 4/11/2017.
  */
class Port(port_type: PortType, capacity: Int) extends Actor {
  var signal: Signal = Signal.empty(capacity)
  var spouse: Option[Actor] = None

  def connect(actor: Actor): Boolean = {
    val cond = connection_precondition(actor)
    if (cond){
      this.spouse = Some(actor)
      actor.self ! SetSpouse(this)
    }
    cond
  }

  def connection_precondition(actor: Actor): Boolean = {
    val future_capacity = actor.self ? Capacity
    val future_type     = actor.self ? PortType
    val cond: Future[Boolean] = for {
      other_capacity <- future_capacity
      other_type <- future_type
    } yield (this.capacity == other_capacity) && (this.port_type == other_type)
    Await.result(cond, 50 millis)
  }

  override def receive = {
    case Capacity => sender ! capacity
    case PortType => sender ! port_type
    case GetSignal => sender ! signal
    case SetSpouse(actor) =>
      connect(actor)
    case Transmit =>
      if (port_type == PortType.OUT)
        spouse foreach (_.self ! SetSignal(this.signal))
    case SetSignal(signal: Signal) =>
        this.signal = signal
  }

  override def toString: String = s"Port: $port_type -> $capacity"
}

object Port {

  object PortType extends Enumeration {
    type PortType = Value
    val IN, OUT, UNUSED = Value
  }

  case object Capacity
  case object Transmit
  case object GetSignal
  case class SetSpouse(actor: Actor)
  case class SetSignal(signal: Signal)

  private def inst(port_type: PortType, capacity: Int): ActorRef = {
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
