package org.mephi.cquiz.loops

import org.junit.Test
import org.mephi.cquiz.{Expressions, Main}

class ExpressionsTest {
  @Test
  def test() {
    println("answer: " + Main.answer(Expressions.nextQuestion()))
  }
}
