package org.mephi.cquiz

object NullWriter extends Writer {
  override def write(o: Any): Writer = this

  override def nextLine(): Writer = this

  override def block(body: => Any): Writer = this
}
