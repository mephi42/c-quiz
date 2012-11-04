package bootstrap.liftweb

import net.liftweb._

import common.Full
import http._

class Boot {
  def boot() {
    LiftRules.addToPackages("org.mephi.cquiz.liftweb")
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("in-progress").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("in-progress").cmd)
    LiftRules.ajaxPostTimeout = 60000
  }
}
