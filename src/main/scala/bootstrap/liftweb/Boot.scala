package bootstrap.liftweb

import net.liftweb._

import http._

class Boot {
  def boot() {
    LiftRules.addToPackages("org.mephi.cquiz.liftweb")
  }
}
