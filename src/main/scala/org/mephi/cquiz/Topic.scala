package org.mephi.cquiz

trait Topic {
  def id: String

  def title: String

  def description: String

  def nextQuestion(): Question
}
