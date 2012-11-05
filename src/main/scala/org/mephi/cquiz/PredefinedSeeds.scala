package org.mephi.cquiz

import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import dispatch.json.JsonParser
import util.parsing.input.StreamReader
import java.io.InputStreamReader

object PredefinedSeeds {
  val seeds: Option[Map[String, Array[(Long, String)]]] = {
    val stream = getClass.getResourceAsStream("Seeds")
    if (stream == null) {
      None
    } else {
      Some(fromjson[Map[String, Array[(Long, String)]]](JsonParser(StreamReader(new InputStreamReader(stream)))))
    }
  }
}
