package org.mephi.cquiz

import java.nio.file.Files
import java.io.{PrintStream, File}
import scala.sys.process._
import io.Source

class Main extends App {
}

object Main {
  def program(quiz: Quiz, writer: Writer) {
    writer.write("#include <stdio.h>").nextLine()
    writer.write("int main() ").block {
      quiz.question(writer)
      writer.write("return 0;").nextLine()
    }.nextLine().nextLine()
  }

  implicit def richProcessBuilder(pb: ProcessBuilder) = new {
    def !!!() {
      val code = pb.!
      if (code != 0) sys.error("Nonzero exit value: " + code)
    }
  }

  def answer(quiz: Quiz): Option[String] = {
    val dir = Files.createTempDirectory("c-quiz").toFile

    val src = new File(dir, "c-quiz.c")
    val srcStream = new PrintStream(src)
    try {
      program(quiz, new DefaultWriter(srcStream))
    } finally {
      srcStream.close()
    }

    val bin = new File(dir, "c-quiz")
    Process(Seq("gcc", "-O0", "-g3", "-o", bin.toString, src.toString)).!!!

    val pyScript = new File(dir, "c-quiz.py")
    val out = new File(dir, "c-quiz.out")
    val pyStream = new PrintStream(pyScript)
    try {
      pyStream.print(
        s"""
          |gdb.execute("set args > $out")
          |gdb.execute("set backtrace past-main")
          |
          |mainBp = gdb.Breakpoint("main")
          |mainDoneBp = None
          |
          |count = 0
          |def onStop(event):
          |  global count, mainDoneBp
          |  if isinstance(event, gdb.BreakpointEvent):
          |    if mainBp in event.breakpoints:
          |      mainDoneBp = gdb.FinishBreakpoint()
          |    if mainDoneBp != None and mainDoneBp in event.breakpoints:
          |      gdb.execute("c")
          |  count = count + 1
          |  if count > 100:
          |    gdb.execute("quit 1")
          |  gdb.execute("next")
          |gdb.events.stop.connect(onStop)
          |
          |gdb.execute("run")
        """.stripMargin
      )
    } finally {
      pyStream.close()
    }

    val code = Process(Seq("gdb", "--batch", "-x", pyScript.toString, bin.toString)).!
    if (code == 0)
      Some(Source.fromFile(out).getLines().next())
    else
      None
  }
}