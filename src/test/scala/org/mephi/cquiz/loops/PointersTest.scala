package org.mephi.cquiz.loops

import org.junit.Test
import org.mephi.cquiz.{Pointers, Main}

class PointersTest {
  @Test
  def test() {
    println("answer: " + Main.answer(Pointers.nextQuestion()))
  }
}
