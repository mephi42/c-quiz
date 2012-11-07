package org.mephi.cquiz.liftweb.snippet

import net.liftweb.http.{S, Templates, SHtml}
import net.liftweb.util.Helpers._
import xml.NodeSeq
import org.mephi.cquiz.{Topic, DefaultWriter, Main, Topics}
import collection.mutable
import net.liftweb.util.ClearNodes
import net.liftweb.http.js.JsCmds.SetHtml
import java.io.StringWriter
import net.liftweb.common.Full

class Select {
  def render = {
    val bundle = Topic.getBundle(S.locale)

    var variantCount = "1"
    val topicCounts = new mutable.HashMap[String, Int]
    var answers = "none"

    def onSubmit = {
      val taskTopics = Topics.topics.flatMap(topic =>
        topicCounts.get(topic.id).toList.flatMap(topicCount =>
          List.fill(topicCount)(topic)
        )).toArray
      val variants = (1 to variantCount.toInt).par.map(variantNumber =>
        taskTopics.par.map(topic => Main.makeTask(topic)).toArray).toArray

      val tasksTemplate = Templates(List("templates-hidden", "tasks")).getOrElse(sys.error( """template "tasks" not found"""))
      val variantsTransform = "div id=variant *" #> {
        (1 to variants.length).map(variantNumber => {
          val numberTransform = "span id=variantNumber *" #> variantNumber.toString
          val variant = variants(variantNumber - 1)
          val tasksTransform = "div id=task *" #> {
            (1 to variant.length).map(taskNumber => {
              val numberTransform = "span id=taskNumber" #> taskNumber.toString
              val task = variant(taskNumber - 1)
              val questionTransform = "pre id=code *" #> {
                val writer = new StringWriter
                task.question.write(new DefaultWriter(writer))
                writer.toString
              }
              val answerTransform = "div id=maybeAnswer" #> (if (answers == "each") {
                "tt id=answer *" #> task.answer
              } else {
                ClearNodes
              })
              (numberTransform `compose` questionTransform `compose` answerTransform)
            })
          }
          (numberTransform `compose` tasksTransform)
        })
      }
      val tasksXhtml = variantsTransform(tasksTemplate)

      val answersXhtml = if (answers == "all") {
        val answersTemplate = Templates(List("templates-hidden", "answers")).getOrElse(sys.error( """template "answers" not found"""))
        val variantsTransform = "div id=variant *" #> {
          (1 to variants.length).map(variantNumber => {
            val numberTransform = "span id=variantNumber *" #> variantNumber.toString
            val variant = variants(variantNumber - 1)
            val tasksTransform = "div id=task *" #> {
              (1 to variant.length).map(taskNumber => {
                val numberTransform = "span id=taskNumber" #> taskNumber.toString
                val task = variant(taskNumber - 1)
                val answerTransform = "tt id=answer *" #> task.answer
                numberTransform `compose` answerTransform
              })
            }
            numberTransform `compose` tasksTransform
          })
        }
        Some(variantsTransform(answersTemplate))
      } else {
        None
      }

      SetHtml("maybeTasks", (tasksXhtml ++ answersXhtml).flatten)
    }

    val variantCountTransform = "td id=variantCount *" #> SHtml.text(variantCount, variantCount = _, "size" -> "3")
    val topicCountsTransform = "tr id=topics" #> {
      (tr: NodeSeq) => {
        Topics.topics.map(topic => {
          val titleTransform = "span id=title *" #> bundle.getString(topic.titleKey)
          val descriptionTransform = "span id=description *" #> bundle.getString(topic.descriptionKey)
          val countTransform = "td id=count *" #> SHtml.text("1", v => topicCounts.put(topic.id, v.toInt), "size" -> "3")
          (titleTransform `compose` descriptionTransform `compose` countTransform)(tr)
        })
      }
    }
    val answersTypeTransform = "td id=answersType" #> SHtml.select(Seq(("none", S.?("noAnswers")), ("each", S.?("afterEach")), ("all", S.?("afterAll"))), Full(answers), answers = _)
    val submitTransform = "td id=submit" #> SHtml.ajaxSubmit(S.?("getTest"), () => onSubmit)
    val quizTransform = "div id=maybeTasks *" #> ClearNodes

    variantCountTransform `compose` topicCountsTransform `compose` answersTypeTransform `compose` submitTransform `compose` quizTransform
  }
}
