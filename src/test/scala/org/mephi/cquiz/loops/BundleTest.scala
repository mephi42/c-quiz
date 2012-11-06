package org.mephi.cquiz.loops

import org.junit.Test
import org.mephi.cquiz.{Expressions, Topic}
import java.util.Locale

class BundleTest {
  @Test
  def testRu() {
    println(Topic.getBundle(new Locale("ru")).getString(Expressions.titleKey))
  }
}
