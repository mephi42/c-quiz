package org.mephi.cquiz

import org.junit.Test

class PointerArithmeticTest {
  @Test
  def test() {
    val question = PointerArithmetic.nextQuestion()
    question.writeCode(new DefaultWriter(Console.out))
    println("answer: " + question.answer)
  }
}
