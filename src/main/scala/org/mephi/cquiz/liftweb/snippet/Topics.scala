package org.mephi.cquiz.liftweb.snippet

import net.liftweb.util._
import Helpers._

class Topics {
  def render = "li *" #> org.mephi.cquiz.Topics.topics.map(t => t.title)
}
