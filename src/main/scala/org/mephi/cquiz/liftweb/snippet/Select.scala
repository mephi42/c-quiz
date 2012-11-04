package org.mephi.cquiz.liftweb.snippet

import net.liftweb.http.{Templates, SHtml}
import net.liftweb.util.Helpers._
import xml.NodeSeq
import org.mephi.cquiz.{DefaultWriter, Main, Topic, Topics}
import collection.mutable
import net.liftweb.util.ClearNodes
import net.liftweb.http.js.JsCmds.SetHtml
import java.io.StringWriter

class Select {
  def render = {
    var variantCount = "1"
    val topicCounts = new mutable.HashMap[String, Int]

    def onSubmit = {
      val template = Templates(List("templates-hidden", "quiz")).getOrElse(sys.error( """template "quiz" not found"""))
      val taskTopics = Topics.topics.flatMap(topic => {
        topicCounts.get(topic.id).toList.flatMap(topicCount => {
          List.fill(topicCount)(topic)
        })
      })
      val xhtml = (1 to variantCount.toInt).flatMap(variantNumber => {
        val variantNumberTransform = "span id=variantNumber *" #> variantNumber.toString
        val taskTransform = "div id=task *" #> {
          (taskDiv: NodeSeq) =>
            taskTopics.zipWithIndex.map {
              case ((taskTopic: Topic, taskIndex: Int)) =>
                val taskNumberTransform = "span id=taskNumber" #> (taskIndex + 1).toString
                val taskTransform = "pre id=code *" #> {
                  val task = Main.makeTask(taskTopic)
                  val writer = new StringWriter
                  task.question.write(new DefaultWriter(writer))
                  writer.toString
                }
                (taskNumberTransform `compose` taskTransform)(taskDiv)
            }
        }
        (variantNumberTransform `compose` taskTransform)(template)
      }).flatten
      SetHtml("quiz", xhtml)
    }

    val variantCountTransform = "td id=variantCount *" #> SHtml.text(variantCount, variantCount = _, "size" -> "3")
    val topicCountsTransform = "tr id=topics" #> {
      (tr: NodeSeq) => {
        Topics.topics.map(topic => {
          val titleTransform = "td id=title *" #> topic.title
          val countTransform = "td id=count *" #> SHtml.text("1", v => topicCounts.put(topic.id, v.toInt), "size" -> "3")
          (titleTransform `compose` countTransform)(tr)
        })
      }
    }
    val submitTransform = "td id=submit" #> SHtml.ajaxSubmit("Получить тест", () => onSubmit)
    val quizTransform = "div id=quiz *" #> ClearNodes

    variantCountTransform `compose` topicCountsTransform `compose` submitTransform `compose` quizTransform
  }
}
