package org.mephi.cquiz

trait Question {
  def write(writer: Writer)

  def seed: Long
}
