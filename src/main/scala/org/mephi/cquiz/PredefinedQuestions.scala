package org.mephi.cquiz

import sjson.json._
import DefaultProtocol._
import JsonSerialization._
import dispatch.json.JsonParser
import util.parsing.input.StreamReader
import java.io.InputStreamReader

case class PredefinedQuestion(_text: String, _answer: String) extends Question {
  override def writeCode(code: Writer) {
    code.write(_text)
  }

  def answer: Option[String] = Some(_answer)
}

object PredefinedQuestions {
  val questions: Option[Map[String, Array[PredefinedQuestion]]] = {
    val stream = getClass.getResourceAsStream("questions")
    if (stream == null) {
      None
    } else {
      val parser = JsonParser(StreamReader(new InputStreamReader(stream)))
      val raw = fromjson[Map[String, Array[(String, String)]]](parser)
      Some(raw.mapValues(_.map(pair => PredefinedQuestion(pair._1, pair._2))))
    }
  }
}
