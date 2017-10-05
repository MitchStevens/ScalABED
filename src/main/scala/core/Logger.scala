package core

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.cluster.Cluster
import main.scala.core.Action

import scala.collection.mutable

object Logger {
  private var actions_cached: Int = 100
  private val actions  = mutable.Queue.empty[Action]
  private val handlers = mutable.ArrayBuffer.empty[PartialFunction[Action, Unit]]

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

  add_handler {
    case x => println(x)
  }

}
