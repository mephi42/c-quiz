package org.mephi.cquiz

import x86.Decryption

object Topics {
  val topics = Seq(Expressions, Loops, Pointers, PointerArithmetic, Decryption)
  private val topicMap = topics.map(t => (t.id, t)).toMap

  def apply(topicName: String): Topic = topicMap.getOrElse(topicName, sys.error("No such topic"))
}
