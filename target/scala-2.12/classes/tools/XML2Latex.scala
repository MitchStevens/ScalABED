import scala.xml.{Elem, NodeSeq, Source}

object XML2Latex {

  def main(args: Array[String]): Unit = {
    val input = Source.fromInputStream(System.in)

    val data: Elem = scala.xml.XML.load(input)
    val levels_data: Seq[String] =
      (data \ "level_set") map { level_set =>
        val name = level_set \@ "name"
        val a = (level_set \ "level")
          .map(level_to_latex)
          .mkString("\n")

        s"\\section{$name}\n" ++ a
      }

    val lines =
      "\\documentclass[a4paper, 12pt]{article}" ::
        "\\begin{document}" ::
        "\\tableofcontents" ::
        levels_data.mkString("\n") ++
        "\\end{document}" :: Nil

    println(lines.mkString("\n"))
  }

  def level_to_latex(nodeseq: NodeSeq): String = {
    val name = nodeseq \@ "name"

    s"\\subsection{$name}\n" ++
    io_parse(nodeseq \ "io", nodeseq \@ "min_size") ++
    "\\subparagraph{Instruction Text}\n" ++
      (nodeseq \ "instruction_text").text ++
    "\\subparagraph{Tests}\n" ++
    tests_parse(nodeseq \ "tests") ++
    "\\subparagraph{Completion Text}\n" ++
      (nodeseq \ "completion_text").text ++
    "\\subparagraph{Upon Completion}\n" ++
    upon_completion_parse(nodeseq \ "upon_completion")
  }

  def io_parse(xml: NodeSeq, min_size: String): String = {
    val ins  = (xml \ "input") .map("("+ _.text +")").mkString(" ")
    val outs = (xml \ "output").map("("+ _.text +")").mkString(" ")
    "\\begin{center}" ++
    "\\begin{tabular}{||c|c|c||}\n" ++
    "\\hline" ++
    "\tMin Size & Accepts & Returns \\\\\n" ++
    "\\hline\\hline" ++
    s"\t$min_size & $ins & $outs \\\\\n" ++
    "\\hline" ++
    "\\end{tabular}\n" ++
    "\\end{center}"
  }

  def tests_parse(xml: NodeSeq): String = {
    val ts = (xml \ "test")
      .map(_.text.replace("->", " & "))
      .map(str => "\t" ++ str)
      .mkString(" \\\\\n")

    "\\begin{tabular}{cc}\n" ++
    "\tInput & Output \\\\\n" ++
    ts ++
    "\n\\end{tabular}\n"
  }

  def upon_completion_parse(nodeseq: NodeSeq): String = {
    val cs = (nodeseq \ "circuit_reward")
      .map(c => s"Unlock circuit '${c.text}'")
    val ls = (nodeseq \ "level_reward")
      .map(c => s"Unlock level '${c.text}'")

    (cs ++ ls).mkString(", ") ++ "\n"
  }
}

