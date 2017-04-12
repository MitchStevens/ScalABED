package core

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by Mitch on 4/6/2017.
  */
object ConcurrencyContext {
  implicit val actor_system = ActorSystem("system")
  implicit val timeout = Timeout(1 seconds)
}
