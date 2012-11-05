package org.mephi.cquiz

trait Topic {
  def id: String

  def title: String

  def description: String

  def question(seed: Long): Question

  def nextQuestion() = question(Rng.nextLong())
}
