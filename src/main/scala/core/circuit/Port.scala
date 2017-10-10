package core.circuit

import core.circuit.Port.ConnectionType.ConnectionType
import core.circuit.Port._
import core.circuit.Port.PortType.PortType
import core.types.Signal
import core.types.Signal.Signal

/**
  * Created by Mitch on 4/11/2017.
  */
case class Port private (val port_type: PortType, val capacity: Int) {
  def is_input:  Boolean = port_type == PortType.IN
  def is_output: Boolean = port_type == PortType.OUT
  def is_unused: Boolean = port_type == PortType.UNUSED

  override def toString: String = s"Port ${port_type.toString.padTo(6, ' ')} with capacity $capacity."

  override def equals(obj: Any): Boolean =
    obj match {
      case that: Port =>
        (that.capacity == this.capacity) && (that.port_type == this.port_type)
      case _ => false
    }
}

object Port {
  final object PortType extends Enumeration {
    type PortType = Value
    val IN, OUT, UNUSED = Value
  }

  final object ConnectionType extends Enumeration {
    type ConnectionType = Value
    val TransmitsTo, ReceivesFrom = Value
  }

  //These are the only way to create a port
  def in(capacity: Int):  Port = new Port(PortType.IN,  capacity)
  def out(capacity: Int): Port = new Port(PortType.OUT, capacity)
  def unused:             Port = new Port(PortType.UNUSED, 0)

  def create(i: Int, o: Int): Port = create((i, o))
  def create(io: (Int, Int)): Port = {
    val (ins, outs) = io
    if (ins >  0 && outs == 0)
      new Port(PortType.IN, ins)
    else if (ins == 0 && outs > 0)
      new Port(PortType.OUT, outs)
    else if (ins == 0 && outs == 0)
      new Port(PortType.UNUSED, 0)
    else throw new Error(s"Couldn't create a port with $ins inputs and $outs outputs.")
  }

  def connection_type(p1: Port, p2: Port): Option[ConnectionType] = {
    val same_capacity: Boolean = p1.capacity == p2.capacity
    val connection_type: Option[ConnectionType] = (p1.port_type, p2.port_type) match {
      case (PortType.OUT, PortType.IN) =>  Some(ConnectionType.TransmitsTo)
      case (PortType.IN,  PortType.OUT) => Some(ConnectionType.ReceivesFrom)
      case _         => None
    }
    if (same_capacity)
      connection_type
    else None
  }
}