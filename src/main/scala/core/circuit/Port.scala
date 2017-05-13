package core.circuit

import core.circuit.Port._
import core.circuit.Port.PortType.PortType
import core.types.Signal
import core.types.Signal.Signal
/**
  * Created by Mitch on 4/11/2017.
  */
class Port(val port_type: PortType, val capacity: Int) {
  private var signal: Signal = Signal.empty(capacity)
  private var spouse: Option[Port] = None

  def connect_to(port: Port): Boolean = {
    if (this eq port) return false

    val cond = connection_precondition(port)
    if (cond){
      this.spouse = Some(port)
      port.set_spouse(this)
    }
    cond
  }

  def connection_precondition(port: Port): Boolean = {
    def capacity_condition(other_capacity: Any): Boolean = {this.capacity == other_capacity}
    def port_type_condition(other_type: Any): Boolean = this.port_type match {
      case PortType.IN  => false
      case PortType.OUT => other_type == PortType.IN
      case PortType.UNUSED => false
    }

    capacity_condition(port.capacity) && port_type_condition(port.port_type)
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
  def set_spouse(spouse: Port) = {this.spouse = Some(spouse)}
  def remove_spouse() {
    this.spouse = None
    if (is_input)
      signal = Signal.empty(capacity)
  }

  def get_input: Signal = {
    if (is_input) signal
    else Signal.empty(0)
  }

  def set_input(signal: Signal) = {
    if(is_input)
      this.signal = signal
  }

  def get_output: Signal =
    if (is_output) signal
    else Signal.empty(0)

  def set_output(signal: Signal) = {
    if (is_output){
      this.signal = signal
      spouse foreach (_.set_input(signal))
    }
  }

  def is_input:  Boolean = {port_type == PortType.IN}
  def is_output: Boolean = {port_type == PortType.OUT}

  override def toString: String = s"Port: $port_type -> $capacity"
}

object Port {
  final object PortType extends Enumeration {
    type PortType = Value
    val IN, OUT, UNUSED = Value
  }

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
}
