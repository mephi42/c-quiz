package org.mephi.cquiz

object Topics {
  private val topics = Seq(Loops)
  private val topicMap = topics.map(t => (t.id, t)).toMap

  def apply(topicName: String): Topic = topicMap.getOrElse(topicName, sys.error("No such topic"))
}
