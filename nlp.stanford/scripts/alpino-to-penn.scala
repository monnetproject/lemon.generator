// First obtain the alpino treebank from http://www.let.rug.nl/~vannoord/ftp/AlpinoCDROM/
// And unzip to it ./AlpinoCDROM

import java.io._
import scala.xml._

val cdbDir = new File("AlpinoCDROM")
val out = new PrintStream("train/alpino.tags")

def buildIndex(node : Node) : Map[String,Seq[Node]] = {
  ((node \\ "node") flatMap {
      subnode => (subnode \ "@index").headOption match {
        case Some(idx) => Some((idx.text,node))
        case None => None
      }
    } groupBy (_._1) map {
      case (x,ys) => (x,ys map (_._2))
    }).toMap
}

def printAsPenn(node : Node) : String = printAsPenn(node, buildIndex(node), 0)

def printAsPenn(node : Node, index : Map[String,Seq[Node]], tab : Int = 0) : String = {
  val sb = new StringBuilder
  ((node \ "@cat") ++ (node \ "@pos")).headOption match {
    case Some(w) => { 
          val cat = w.text.toUpperCase
          val pos = ((node \ "@pos").headOption match {
            case Some(w) => w.text
            case None => ""
          }).toUpperCase
          val word = (node \ "@word").headOption match {
            case Some(w) => Some(w.text.replaceAll("\"",""))
            case None => None
          }
          for(i <- 1 to tab) {
            sb.append(" ")
          }
          word match {
            case Some(w) => {
	      if(cat != "") {
                sb.append("("+cat+ " \""+word.get+"\"")
              } else {
                sb.append("("+pos+ "P \""+word.get+"\"")
              }
              out.print(word.get.replace("\\s","_")+"\\" + (if(pos!=""){pos}else{cat}) + " ")
            }
            case None => {
                sb.append("("+cat)
              }
          }
          for(subnode <- node \ "node") {
            if((subnode \ "@begin").headOption != None) {
              val s = printAsPenn(subnode,index,tab+1)
              if(!(s matches "\\s*")) {
                sb.append("\n")
                sb.append(s)
              }
            }
          }
          sb.append(")")
          sb.toString()
        }
    case None => {
        val idx = (node \ "@index").text
        index.getOrElse(idx,Nil) find {
          node => (node \ "@cat").headOption != None
        } match {
          case Some(subnode) => printAsPenn(subnode,index,tab+1)
          case None => ""
        }
      }
  }
}

for(cdbDir2 <- cdbDir.listFiles() if cdbDir2.isDirectory()) {
  for(xmlFile <- cdbDir2.listFiles() if xmlFile.getName().endsWith("xml")) {
    val xml = XML.loadFile(xmlFile)
    //println((xml \ "sentence").text + " " + xmlFile.getPath())
    println(printAsPenn((xml\"node").head))
    println("")
    out.println()
  }
}
