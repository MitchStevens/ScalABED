package check

import check.generators._

trait Generators
  extends GenCoord
  with    GenDirection
  with    GenExpression
  with    GenGameTest
  with    GenSignal
  with    GenGraph
  with    GenLevel
  with    GenPort