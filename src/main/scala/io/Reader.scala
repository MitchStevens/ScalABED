package io

import core.circuit.Mapping
import java.util.{List => JavaList, ArrayList => JavaArrayList}

import scala.collection.JavaConverters
import scala.collection.immutable.{HashMap => ScalaMap}
import scala.xml.{Elem, Node, NodeSeq}

/**
  * Created by Mitch on 5/21/2017.
  */
object Reader {
  private val LEVELS_PATH:    String = "res/xml/levels.xml"
  private val MAPPINGS_PATH:  String = "res/xml/mappings.xml"

  val LEVELS: Seq[Seq[Level]] = read_levels()
  val LEVELS_JAVA: JavaList[JavaList[Level]] = {
    val java_list: JavaList[JavaList[Level]] = new JavaArrayList()
    for(list <- LEVELS)
      java_list.add(JavaConverters.seqAsJavaList(list))
    java_list
  }

  val MAPPINGS: ScalaMap[String, Mapping] = read_mappings()


  private def read_levels(): Seq[Seq[Level]] = {
    val data: Elem = scala.xml.XML.loadFile(LEVELS_PATH)
    (data \ "level_set") map {
      _ \ "level" map {
        create_level
      }
    }

  }

  private def create_level(nodeseq: NodeSeq): Level  = {
    def inputs(xml: NodeSeq): Array[Int] = {
      val ins :Array[Int] = Array(0, 0, 0, 0)
      for (i <- (xml \ "input") map (_.text.split(" "))){
        val index = Array("Up", "Right", "Down", "Left") indexOf i(0)
        ins(index) = i(1).toInt
      }
      ins
    }
    def outputs(xml: NodeSeq): Array[Int] = {
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
  }

  private def read_mappings(): ScalaMap[String, Mapping] = {
    def f(xml: NodeSeq): (String, Mapping) =
      (xml \@ "name", new Mapping(xml \@ "inputs", xml \@ "evals"))

    val data: Elem = scala.xml.XML.loadFile(MAPPINGS_PATH)
    ScalaMap.empty[String, Mapping] ++ ((data \ "mapping") map f)
  }
}
