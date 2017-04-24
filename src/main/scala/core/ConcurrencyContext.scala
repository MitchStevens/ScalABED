package core

import akka.actor.ActorSystem
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by Mitch on 4/6/2017.
  */
object ConcurrencyContext {
  implicit val actor_system = ActorSystem("system")

  val future_wait: FiniteDuration = 1000 millis
  implicit val timeout = Timeout(future_wait)
}
