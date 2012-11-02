package org.mephi.cquiz

import loops.Loops
import util.Random

object Topics {
  private val rng = new Random
  private val map = Map[String, () => Question](
    ("loops", () => new Loops(rng.nextLong()))
  )

  def apply(s: String) = map.getOrElse(s, sys.error("No such topic"))
}
