package core.circuit

import javafx.beans
import javafx.beans.Observable

import core.circuit.Port._
import core.circuit.Port.PortType.PortType
import core.types.Signal
import core.types.Signal.Signal

import scalafx.beans.property.Property

/**
  * Created by Mitch on 4/11/2017.
  */
class Port private (val port_type: PortType, val capacity: Int) {
  private var signal: Signal = Signal.empty(capacity)
  private var spouse: Option[Port] = None

  def connect_to(port: Port): Boolean = {
    if (this eq port) return false

    val cond = connection_precondition(this, port)
    if (cond){
      this.spouse = Some(port)
      port.set_spouse(this)
    }
    cond
  }

  def disconnect_from(port: Port): Boolean = {
    if (this eq port) return false

    val has_connection: Boolean =
      spouse.exists(port.eq)
    if (has_connection){
      port.remove_spouse()
      this.remove_spouse()
      set_input(Signal.empty(capacity))
    }
    has_connection
  }

  def get_spouse: Option[Port] = this.spouse
  def set_spouse(spouse: Port): Unit =
    this.spouse = Some(spouse)

  def remove_spouse() {
    this.spouse = None
    if (is_input)
      signal = Signal.empty(capacity)
  }

  def get_input: Signal = {
    if (is_input) signal
    else Signal.empty(0)
  }

  def set_input(signal: Signal): Unit =
    if(is_input) {
      this.signal = signal
    }

  def get_output: Signal =
    if (is_output) signal
    else Signal.empty(0)

  def set_output(signal: Signal): Unit = {
    if (is_output){
      this.signal = signal
      spouse foreach (_.set_input(signal))
    }
  }

  def is_input:  Boolean = {port_type == PortType.IN}
  def is_output: Boolean = {port_type == PortType.OUT}

  override def toString: String = s"Port ${port_type.toString.padTo(6, ' ')} with capacity $capacity. Current value: $signal"

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

  private def connection_precondition(p1: Port, p2: Port): Boolean = {
    val capacity_condition: Boolean = p1.capacity == p2.capacity
    val port_type_condition: Boolean = p1.port_type == PortType.OUT && p2.port_type == PortType.IN

    capacity_condition && port_type_condition
  }
}
