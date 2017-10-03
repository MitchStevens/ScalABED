package core

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import main.scala.core.Action

import scala.collection.mutable

class Logger extends Actor {
  def receive = {
    case action: Action => {println(action)}
    case _              => {}
  }
}

object Logger {
  private val system = ActorSystem("HelloSystem")
  private var actions_cached: Int = 100
  private val actions  = mutable.Queue.empty[Action]
  private val handlers = mutable.ArrayBuffer.empty[PartialFunction[Action, Unit]]
  private val logger_actor = system.actorOf(Props[Logger])

  def add_action(action: Action): Unit = {
    actions += action
    for (handler <- handlers)
      handler(action)
  }

  def add_handler(handler: PartialFunction[Action, Unit]): Unit = {
    handlers += handler
  }

  /**
    * Get the last `n` actions that have been logged.
    */
  def last_n(n: Int): List[Action] = ???
  def add_listener(ref: ActorRef)  = ???

  add_handler {
    case x => println(x)
  }

}
