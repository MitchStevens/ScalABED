package graphics

import javafx.scene.layout.{Pane, Region}

/**
  * Created by Mitch on 5/18/2017.
  */
trait Resizable {
  this: Region =>

  this.parentProperty().addListener{_ =>
    val parent: Region = this.getParent.asInstanceOf[Region]
    this.prefWidthProperty  bind parent.widthProperty
    this.prefHeightProperty bind parent.heightProperty
  }
}

