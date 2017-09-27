package graphical.controls

import javafx.scene.{control => jfxsc}

import io.Reader
import org.controlsfx.control.textfield.TextFields
import collection.JavaConverters._
import scalafx.scene.control.TextField

class AutoCompleteField(override val delegate: jfxsc.TextField = new jfxsc.TextField()) extends TextField {

  TextFields.bindAutoCompletion(delegate, Reader.MAPPINGS.keySet.asJava)

}
