package org.mephi.cquiz.loops

import org.junit.Test
import org.mephi.cquiz.{Loops, Main}

class LoopsTest {
  @Test
  def test() {
    println("answer: " + Main.answer(Loops.nextQuestion()))
  }
}
