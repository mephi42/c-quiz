package org.mephi.cquiz

import java.io.{StringReader, StringWriter, PrintStream, File}
import org.apache.commons.io.input.ReaderInputStream
import scala.io.Source
import scala.sys.process.Process
import java.nio.file.Files
import org.apache.commons.io.FileUtils
import RichProcessBuilder._

class GdbRunner {
  val dir = Files.createTempDirectory("c-quiz").toFile
  val src = new File(dir, "c-quiz.c")
  val bin = new File(dir, "c-quiz")

  def close() {
    if (!"true".equals(System.getProperty("org.mephi.cquiz.keepTemp"))) {
      FileUtils.deleteDirectory(dir)
    }
  }

  def result(writeAll: (Writer, Writer) => Unit, minSteps: Int, maxSteps: Int): Option[String] = {
    val srcStream = new PrintStream(src)
    val input = new StringWriter()
    try {
      writeAll(new DefaultWriter(srcStream), new DefaultWriter(input))
    } finally {
      srcStream.close()
    }

    Process(Seq("gcc", "-O0", "-g3", "-o", bin.toString, src.toString)).!!!

    val pyScript = new File(dir, "c-quiz.py")
    val out = new File(dir, "c-quiz.out")
    val pyStream = new PrintStream(pyScript)
    try {
      pyStream.print(
        """
          |gdb.execute("set args > %s")
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
          |      if count < %d:
          |        gdb.execute("quit 1")
          |      gdb.execute("c")
          |  count = count + 1
          |  if count > %d:
          |    gdb.execute("quit 1")
          |  gdb.execute("next")
          |gdb.events.stop.connect(onStop)
          |
          |gdb.execute("run")
        """.stripMargin.format(out, minSteps, maxSteps))
    } finally {
      pyStream.close()
    }

    val gdb = Seq("gdb", "--batch", "-x", pyScript.toString, bin.toString)
    val code = (Process(gdb) #< new ReaderInputStream(new StringReader(input.toString))).!
    if (code == 0)
      Some(Source.fromFile(out).getLines().next())
    else
      None
  }
}
