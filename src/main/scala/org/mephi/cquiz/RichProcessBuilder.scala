package org.mephi.cquiz

import scala.sys.process.ProcessBuilder

object RichProcessBuilder {
  implicit def richProcessBuilder(pb: ProcessBuilder) = new {
    def !!!() {
      val code = pb.!
      if (code != 0) sys.error("Nonzero exit value: " + code)
    }
  }
}
