package org.mephi.cquiz

trait Question {
  /** Writes complete quiz program code. */
  def writeCode(code: Writer)

  /** Returns answer.
    * If question appears to be invalid, returns None. */
  def answer: Option[String]

  def minSteps: Int = 0

  def maxSteps: Int = 100
}
