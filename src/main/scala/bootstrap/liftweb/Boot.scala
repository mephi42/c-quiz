package bootstrap.liftweb

import net.liftweb._

import http._
import sitemap.{SiteMap, Loc, Menu}

class Boot {
  def boot() {
    LiftRules.addToPackages("org.mephi.cquiz.liftweb")

    val testEntries = org.mephi.cquiz.Topics.topics.map(t => Menu(Loc(t.id, List(t.id), t.title)))
    val entries = Seq(Menu(Loc("index", List("index"), "Тесты по Си"), testEntries: _*))

    LiftRules.setSiteMap(SiteMap(entries: _*))
  }
}
