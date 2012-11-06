package org.mephi.cquiz

import java.util.{ResourceBundle, Locale}

trait Topic {
  def id: String

  def titleKey: String

  def descriptionKey: String

  def question(seed: Long): Question

  def nextQuestion() = question(Rng.nextLong())
}

object Topic {
  def getBundle(locale: Locale) = ResourceBundle.getBundle("org.mephi.cquiz.bundle", locale)
}
