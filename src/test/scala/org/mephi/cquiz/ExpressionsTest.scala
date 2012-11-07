package org.mephi.cquiz

import org.junit.Test

class ExpressionsTest {
  @Test
  def test() {
    println("answer: " + Main.answer(Expressions.nextQuestion()))
  }
}
