package io

import cats.Monad
import cats._
import cats.data._
import cats.implicits._
import core.circuit.{Game, MesaCircuit, Port}
import core.types.GameTest
import core.types.Signal.Signal

/**
  * Created by Mitch on 5/21/2017.
  *
  * Decided to use a pure functional style for this class.
  */
case class LevelSet(name: String, levels: Seq[Level])

case class Level( name: String,
                  optimal_size: Int,
                  instruction_text: String,
                  completion_text: String,
                  ports: Array[Port],
                  tests: Seq[GameTest] ){

  def inputs:  Array[Int] = ports.map(p => if (p.is_input) p.capacity else 0)
  def outputs: Array[Int] = ports.map(p => if (p.is_output) p.capacity else 0)

  def is_complete(game: Game): LevelCompletion = {
    val completion_status: Either[LevelCompletion, MesaCircuit] =
      Right(game.mesa_circuit) flatMap
      correct_inputs  flatMap
      correct_outputs flatMap
      run_tests

    completion_status match {
      case Left(level_completion) => level_completion
      case Right(_)            => is_optimal(game)
    }
  }

  private def correct_inputs(mesa: MesaCircuit): Either[LevelCompletion, MesaCircuit] =
    if (this.inputs sameElements mesa.num_input_array)
      Right(mesa)
    else Left(WrongInputs(this.inputs, mesa.num_input_array))

  private def correct_outputs(mesa: MesaCircuit): Either[LevelCompletion, MesaCircuit] =
    if (this.outputs sameElements mesa.num_output_array)
      Right(mesa)
    else Left(WrongOutputs(this.outputs, mesa.num_output_array))

  private def run_tests(mesa: MesaCircuit): Either[LevelCompletion, MesaCircuit] =
    tests.foldRight(Either.right[LevelCompletion, MesaCircuit](mesa)){
      (test, mesa) => mesa >>= test
    }

  private def is_optimal(game: Game): Completed =
    math.signum (optimal_size - game.size) match {
      case -1 => SuperOptimal(optimal_size, game.size)
      case 0  => Optimal(optimal_size)
      case 1  => SubOptimal(optimal_size, game.size)
    }

  override def toString: String = {
    val max_len = 32
    val inst =
      if (instruction_text.length > max_len)
        instruction_text.substring(0, max_len)++"..."
      else instruction_text
    val comp =
      if (completion_text.length > max_len)
        completion_text.substring(0, max_len)++"..."
      else completion_text

    s"""Level: $name
      |Optimal Size: $optimal_size
      |Instruction Text: $inst
      |Completion Text: $comp
      |Ports: ${ports foreach println}
    """.stripMargin
  }
}

sealed trait LevelCompletion
  sealed trait Completed extends LevelCompletion
    case class SuperOptimal(optimal: Int, real: Int) extends Completed
    case class Optimal(size: Int)                    extends Completed
    case class SubOptimal(optimal: Int, real: Int)   extends Completed
  case class WrongInputs(expected: Array[Int], found: Array[Int])  extends LevelCompletion
  case class WrongOutputs(expected: Array[Int], found: Array[Int]) extends LevelCompletion
  case class FailedTest(test: GameTest)                            extends LevelCompletion