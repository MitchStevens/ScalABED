package io

import cats.Monad
import cats._
import cats.data._
import cats.implicits._
import core.circuit.{Game, MesaCircuit}
import core.types.GameTest
import core.types.Signal.Signal

/**
  * Created by Mitch on 5/21/2017.
  *
  * Decided to use a pure functional style for this class.
  */
case class Level(name: String,
                 optimal_size: Int,
                 instruction_text: String,
                 completion_text: String,
                 inputs: Array[Int],
                 outputs: Array[Int],
                 tests: List[GameTest]) {

  def is_complete(game: Game): LevelCompletion = {
    val completion_status: Either[LevelCompletion, MesaCircuit] =
      Right(game.mesa_circuit) flatMap
      correct_inputs  flatMap
      correct_outputs flatMap
      run_tests

    completion_status match {
      case Left(level_completion) => level_completion
      case Right(mesa)            => is_optimal(game)
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

}

sealed trait LevelCompletion
  sealed trait Completed extends LevelCompletion
    case class SuperOptimal(optimal: Int, real: Int) extends Completed
    case class Optimal(size: Int)                    extends Completed
    case class SubOptimal(optimal: Int, real: Int)   extends Completed
  case class WrongInputs(expected: Array[Int], found: Array[Int])  extends LevelCompletion
  case class WrongOutputs(expected: Array[Int], found: Array[Int]) extends LevelCompletion
  case class FailedTest(test: GameTest)                            extends LevelCompletion