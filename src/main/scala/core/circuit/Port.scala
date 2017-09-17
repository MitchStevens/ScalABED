package core.circuit

import javafx.beans
import javafx.beans.Observable

import com.sun.glass.ui.View.Capability
import core.circuit.Port._
import core.circuit.Port.PortType.PortType
import core.types.Signal
import core.types.Signal.Signal

import scalafx.beans.property.Property

/**
  * Created by Mitch on 4/11/2017.
  */
trait Port {
  val capacity: Int
  var signal: Signal = Signal.empty(0)

  def connect(that: Port): Boolean = (this, that) match {
    case (PortOut(n1), PortIn(n2)) =>
      if (n1 == n2) {
        that.asInstanceOf[PortIn].source = Some(this)
        true
      } else false
    case _ => false
  }

  def disconnect(that: Port): Boolean = (this, that) match {
    case (PortOut(n1), PortIn(n2)) =>
      if (that.asInstanceOf[PortIn].source == this) {
        that.asInstanceOf[PortIn].source = None
        true
      } else false
    case _ => false
  }

  def value: Signal = signal
  def set_value(s: Signal): Unit =
    if (s.length == capacity)
      signal = s

  override def toString: String = {
    val name = this.getClass.getSimpleName.padTo(6, ' ')
    s"Port $name with capacity $capacity. Current value: $signal"
  }

  override def equals(obj: Any): Boolean =
    obj match {
      case that: Port =>
        (this.capacity == that.capacity) && (this.getClass equals that.getClass)
      case _ => false
    }
}

object Port {

  private case class  PortIn(override val capacity: Int) extends Port {
    var source: Option[PortOut] = None
  }
  private case class  PortOut(override val capacity: Int) extends Port
  private case object PortUnused extends Port {
    override val capacity: Int = 0
  }

  final object PortType extends Enumeration {
    type PortType = Value
    val IN, OUT, UNUSED = Value
  }

  //These are the only way to create a port
  def in(n: Int):          PortIn  = new PortIn(n)
  def in(signal: Signal):  PortIn  = new PortIn(signal.size).se
  def out(n: Int):         PortOut = new PortOut(n,           Signal.empty(n))
  def out(signal: Signal): PortOut = new PortOut(signal.size, signal)
  val unused:              Port = PortUnused

  def create(i: Int, o: Int): Port = create((i, o))
  def create(io: (Int, Int)): Port = {
    val (ins, outs) = io
    if      (ins >  0 && outs == 0) Port.in(ins)
    else if (ins == 0 && outs >  0) Port.out(outs)
    else if (ins == 0 && outs == 0) Port.unused
    else throw new Error(s"Couldn't create a port with $ins inputs and $outs outputs.")
  }
}
