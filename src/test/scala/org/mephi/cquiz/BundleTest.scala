package org.mephi.cquiz

import org.junit.Test
import java.util.Locale

class BundleTest {
  @Test
  def testRu() {
    println(Topic.getBundle(new Locale("ru")).getString(Expressions.titleKey))
  }
}
