package org.mephi.cquiz

trait Writer {
  def write(o: Any): Writer

  def nextLine(): Writer

  def block(body: => Any): Writer
}
