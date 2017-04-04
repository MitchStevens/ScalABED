package core

import akka.actor.{Actor, ActorSystem}

/**
  * Created by Mitch on 3/21/2017.
  */
trait Circuit extends Actor {

}
object Circuit {
  val actor_system = ActorSystem("system")
}
