import java.io._
import scala.io._

val in = new BufferedSource(new FileInputStream("train/alpino.treebank"))

var count = 0
var inQuote = false
var lineNo = 0

for(line <- in.getLines()) {
  if(line matches "\\s*") {
    println(lineNo + " " + count + " " + inQuote)
  } else {
    for(c <- line) {
      if(inQuote) {
        c match {
          case '"' => inQuote = true
          case _ =>
        }
      } else {
        c match {
          case '(' => count += 1
          case ')' => count -= 1
          case '"' => inQuote = false
          case _ =>
        }
      }
    }
  }
  lineNo += 1
}