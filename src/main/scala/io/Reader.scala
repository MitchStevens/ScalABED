package io

import core.circuit._
import java.io.{File, FileInputStream, InputStream}

import JsonInstances._
import play.api.libs.json._

import scala.xml.{Elem, Node, NodeSeq}
import scalafx.scene.image.Image

/**
  * Created by Mitch on 5/21/2017.
  */
object Reader {
  protected object Paths {
    private val root_path = "res/"
    val levels:   String   = root_path + "xml/levels.xml"
    val levelsj:   String  = root_path + "json/levels.json"
    val mappings = root_path + "xml/mappings.xml"
    val images   = root_path + "img"
  }

  val LEVEL_SETS: Seq[LevelSet] = {
    val input_stream: InputStream = new FileInputStream(Paths.levelsj)
    Json.parse(input_stream) match {
      case array: JsArray =>
        array.value map (_.as[LevelSet])
      case _              =>
        throw new Error(s"Couldn't read levelsets from ${Paths.levelsj}")
    }
    //println(Json.parse(input_stream).asInstanceOf[JsArray] \\ "level_set")
  }

  val LEVEL_SET_NAMES: Seq[String]   = read_level_set_names()
  val MAPPINGS: Map[String, Mapping] = read_mappings()
  private val MESACIRCUITS: Map[String, MesaCircuit] = Map.empty[String, MesaCircuit]
  val IMAGES: Map[String, Image]     = read_images()

  def level(i: Int, j: Int): Option[Level] = {println("cocks"); None}

  def evaluable(str: String): Evaluable =
    if (MAPPINGS.contains(str))
      MAPPINGS(str).clone
    else if (MESACIRCUITS.contains(str))
      throw new Error("Not yet implemented")
    else throw new Error(s"Evaluable $str was not found")

  private def create_level(nodeseq: NodeSeq): Level = ??? /* {
    def inputs(xml: NodeSeq): Array[Int] = {
      val ins :Array[Int] = Array(0, 0, 0, 0)
      for (i <- (xml \ "input") map (_.text.split(" "))){
        val index = Array("Up", "Right", "Down", "Left") indexOf i(0)
        ins(index) = i(1).toInt
      }
      ins
    }

    def outputs(xml: NodeSeq): Array[Int] = ??? {
      val ins :Array[Int] = Array(0, 0, 0, 0)
      for (i <- (xml \ "output") map (_.text.split(" "))){
        val index = Array("Up", "Right", "Down", "Left") indexOf i(0)
        ins(index) = i(1).toInt
      }
      ins
    }

    val name = nodeseq \@ "name"
    val min_size = ((s: String) => if (s.isEmpty) 0 else s.toInt) (nodeseq \@ "min_size")
    val instruction_text = (nodeseq \ "instruction_text").text
    val completion_text  = (nodeseq \ "completion_text").text
    val ins  = inputs(nodeseq \ "io")
    val outs = outputs(nodeseq \ "io")

    Level(name, min_size, instruction_text, completion_text, ins, outs, Nil)
  }*/

  private def read_level_set_names(): Seq[String] = {
    val data: Elem = scala.xml.XML.loadFile(Paths.levels)
    (data \ "level_set") map (_ \ "@name" text)
  }

  private def read_mappings(): Map[String, Mapping] = {
    def f(xml: NodeSeq): (String, Mapping) =
      (xml \@ "name", new Mapping(xml \@ "inputs", xml \@ "evals", xml \@ "name"))

    val data: Elem = scala.xml.XML.loadFile(Paths.mappings)
    Map.empty[String, Mapping] ++
    ((data \ "mapping") map f) ++
    List(
      "INPUT"  -> new Input(1),
      "OUTPUT" -> new Output(1)
    )
  }

  private def read_images(): Map[String, Image] = {
    def f(file: File): (String, Image) =
      file.getName.takeWhile(_ != '.') -> new Image(new FileInputStream(file))
    Map.empty[String, Image] ++ (new File(Paths.images).listFiles map f)
  }
}
