package org.mephi.cquiz

import util.Random

trait Topic {
  def id: String

  def title: String

  def description: String

  def question(seed: Long): Question

  def nextQuestion() = question(TopicRng.nextLong())
}

object TopicRng extends Random