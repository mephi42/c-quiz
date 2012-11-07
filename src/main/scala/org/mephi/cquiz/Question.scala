package org.mephi.cquiz

trait Question {
  def write(writer: Writer)

  def seed: Long

  def minSteps: Int = 0

  def maxSteps: Int = 100

  def includes: Iterable[String] = None
}
