package graphics

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.layout.TilePane

import core.types.Coord

import scala.collection.mutable.HashMap
import scala.math.min
/**
  * Created by Mitch on 5/17/2017.
  */
object CircuitPane extends TilePane with Resizable{
  val MIN_SIZE: Int = 3
  val MAX_SIZE: Int = 10

  val squares: HashMap[Coord, Square] = HashMap.empty[Coord, Square]

  var num_tiles: Int = 0
  val tile_size: SimpleDoubleProperty = new SimpleDoubleProperty


  {
    this.setId("circuit_pane")
    this.setTileAlignment(Pos.TOP_LEFT)
    this.getStylesheets.add(getClass().getResource("css/circuit_pane.css").toExternalForm)
  }

  def set_size(n: Int): Unit = {
    if(!(MIN_SIZE <= n && n <= MAX_SIZE)) return

    this.setPrefRows(n)
    tile_size.set(min(this.getWidth, this.getHeight)/n)

    if(n > num_tiles)
      inc_size(n)

    num_tiles = n


  }

  private def inc_size(size: Int): Unit = {
    Coord.over_square(size)
      .filter(_.within(size))
      .foreach { (pos: Coord) =>
        val s: Square = new Square(pos)
        squares += (pos -> s)
        //this.getChildren.add(s)
      }
  }


}
