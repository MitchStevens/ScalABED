#!/bin/bash
scalac XML2Latex.scala
cat "../xml/levels.xml" | scala XML2Latex > levels.tex
latex levels.tex
pdflatex levels.tex

mv levels.pdf "../docs"
mv levels.tex "../docs"

rm levels.aux
rm levels.dvi
rm levels.log
rm levels.toc
rm XML2Latex.class
rm "XML2Latex$.class"