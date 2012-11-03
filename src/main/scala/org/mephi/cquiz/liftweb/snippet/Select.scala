package org.mephi.cquiz.liftweb.snippet

import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import xml.NodeSeq
import org.mephi.cquiz.Topics
import collection.mutable

class Select {
  def render = {
    var variantCount = "1"
    val topicCounts = new mutable.HashMap[String, Int]

    def onSubmit() {
      sys.error("not implemented")
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
    val submitTransform = "td id=submit *" #> SHtml.submit("Получить тест", onSubmit)

    variantCountTransform `compose` topicCountsTransform `compose` submitTransform
  }
}
