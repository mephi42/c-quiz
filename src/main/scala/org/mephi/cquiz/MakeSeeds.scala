package org.mephi.cquiz

import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import java.io.PrintStream
import collection.parallel.mutable.ParArray

object MakeSeeds extends App {
  args match {
    case Array(file, count) => {
      val seedsPerTopic: Int = count.toInt / Topics.topics.length
      val seeds: Map[String, Array[(Long, String)]] = Topics.topics.par.map(topic => {
        (topic.id, ParArray.fill(seedsPerTopic) {
          val task = Main.makeTask(topic)
          (task.question.seed, task.answer)
        }.seq.toArray)
      }).seq.toMap
      val stream = new PrintStream(file)
      try {
        stream.println(tojson(seeds))
      } finally {
        stream.close()
      }
    }
  }
}