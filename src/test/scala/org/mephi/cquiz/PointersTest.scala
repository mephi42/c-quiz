package org.mephi.cquiz

import org.junit.Test

class PointersTest {
  @Test
  def test() {
    val question = Pointers.nextQuestion()
    question.writeCode(new DefaultWriter(Console.out))
    println("answer: " + Pointers.nextQuestion().answer)
  }
}
