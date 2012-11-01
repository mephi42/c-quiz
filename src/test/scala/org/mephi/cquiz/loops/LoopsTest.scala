package org.mephi.cquiz.loops

import org.junit.Test
import org.mephi.cquiz.{Main, DefaultWriter}

class LoopsTest {
  @Test
  def test() {
    Main.program(new Loops(), new DefaultWriter(Console.out))
  }
}
