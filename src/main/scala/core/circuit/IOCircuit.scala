package core.circuit

import core.types.Signal.Signal

/**
  * Created by Mitch on 5/12/2017.
  */
trait IOCircuit {

  val port: Port
  def values: Signal

}
