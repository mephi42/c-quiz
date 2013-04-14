package org.mephi.cquiz

import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import java.io.{StringWriter, PrintStream}
import collection.parallel.mutable.ParArray

object MakeQuestions extends App {
  args match {
    case Array(file, topicSpec, count) => {
      val topics = if (topicSpec == "all") {
        Topics.topics
      } else {
        topicSpec.split(":").map(Topics.apply).toSeq
      }
      val questionsPerTopic: Int = count.toInt / topics.length
      val questions: Map[String, Array[(String, String)]] = topics.par.map(topic => {
        (topic.id, ParArray.fill(questionsPerTopic) {
          val task = Main.makeTask(topic)
          val code = new StringWriter()
          task.question.writeCode(new DefaultWriter(code))
          (code.toString, task.answer)
        }.seq.toArray)
      }).seq.toMap
      val stream = new PrintStream(file)
      try {
        stream.println(tojson(questions))
      } finally {
        stream.close()
      }
    }
  }
}