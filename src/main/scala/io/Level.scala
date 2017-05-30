package io

import core.types.Signal.Signal

/**
  * Created by Mitch on 5/21/2017.
  */
case class Level(name: String,
                 min_size: Int,
                 instruction_text: String,
                 completion_text: String,
                 inputs: Array[Int],
                 outputs: Array[Int],
                 tests: List[(Signal, Signal)]);
