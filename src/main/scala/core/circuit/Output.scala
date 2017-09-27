package core.circuit

import core.types.Direction
import core.circuit.Port.PortType
import core.types.Signal.Signal
/**
  * Created by Mitch on 5/13/2017.
  */
class Output(val capacity: Int) extends Mapping(s"0,0,0,$capacity", "_,_,_,_", "Output") {}