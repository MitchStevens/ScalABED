package io

import core.circuit.Mapping

import scala.xml.{Elem, NodeSeq}

/**
  * Created by Mitch on 5/21/2017.
  */
object Reader {
  private val LEVELS_PATH:    String = "res/xml/levels.xml"
  private val MAPPINGS_PATH:  String = "res/xml/mappings.xml"

  val LEVEL_SETS: Seq[String] = read_level_set_names()
  val LEVELS: Seq[Seq[Level]] = read_levels()

  val MAPPINGS: Seq[Mapping] = read_mappings()

  private def read_level_set_names(): Seq[String] = {
    val data: Elem = scala.xml.XML.loadFile(LEVELS_PATH)
    for (levelset <- data \ "level_set")
       yield (levelset \ "@name").text
  }

  private def read_levels(): Seq[Seq[Level]]  = {
    def to_level(level: NodeSeq): Level = {
      val name = (level \ "@name").text
      val desc = (level \ "instruction_text").text
      Level(name, desc)
    }
    val data: Elem = scala.xml.XML.loadFile(LEVELS_PATH)
    for (level_set <- data \ "level_set")
      yield for (level <- level_set \ "level")
        yield to_level(level)
  }

  private def read_mappings(): Seq[Mapping] = {
    val data: Elem = scala.xml.XML.loadFile(MAPPINGS_PATH)
    for (mapping <- data \ "mapping")
      1+1
    null
  }
}
