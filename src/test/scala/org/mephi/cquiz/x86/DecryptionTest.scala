package org.mephi.cquiz.x86

import org.junit.Test
import org.mephi.cquiz.DefaultWriter

class DecryptionTest {
  @Test
  def test() {
    val question = Decryption.nextQuestion()
    question.writeCode(new DefaultWriter(Console.out))
    Console.out.println()
    println("answer: " + question.answer)
  }
}
