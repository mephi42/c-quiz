package org.mephi.cquiz.liftweb.snippet

import net.liftweb.http.{Templates, SHtml}
import net.liftweb.util.Helpers._
import xml.NodeSeq
import org.mephi.cquiz.Topics
import collection.mutable
import net.liftweb.common.Full
import net.liftweb.util.ClearNodes
import net.liftweb.http.js.JsCmds.SetHtml

class Select {
  def render = {
    var variantCount = "1"
    val topicCounts = new mutable.HashMap[String, Int]

    def onSubmit = {
      val template = Templates(List("quiz")) match {
        case Full(x) => x
        case _ => sys.error( """template "quiz" not found""")
      }
      SetHtml("#quiz", template)
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
