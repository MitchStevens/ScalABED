package test

import core.circuit.MesaCircuit
import org.scalatest.FlatSpec

import scalax.collection.mutable.Graph
import scalax.collection.GraphPredef._

/**
  * Created by Mitch on 9/9/2017.
  */
class TestCircuitGraph extends FlatSpec {

  "The CircuitGraph toposort method" must "evaluate" in {
    val g = Graph('b'~>'a', 'c'~>'b', 'd'~>'c', 'b'~>'d', 'e'~>'c')
    val topo = MesaCircuit.pseudo_toposort(g)
    println(topo)
  }

}
