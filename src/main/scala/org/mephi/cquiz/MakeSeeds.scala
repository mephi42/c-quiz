package org.mephi.cquiz

import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import java.io.PrintStream

object MakeSeeds extends App {
  args match {
    case Array(file, count) => {
      val seedsPerTopic: Int = count.toInt / Topics.topics.length
      val seeds: Map[String, Array[(Long, String)]] = Topics.topics.map(topic => {
        (topic.id, Array.fill(seedsPerTopic) {
          val task = Main.makeTask(topic)
          (task.question.seed, task.answer)
        })
      }).toMap
      val stream = new PrintStream(file)
      try {
        stream.println(tojson(seeds))
      } finally {
        stream.close()
      }
    }
  }
}