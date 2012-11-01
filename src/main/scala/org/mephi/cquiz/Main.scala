package org.mephi.cquiz

import java.nio.file.Files
import java.io.{PrintStream, File}
import loops.Loops
import scala.sys.process._
import io.Source
import util.Random

object Main extends App {
  args match {
    case Array(topic, variants, questions) =>
      val nextQuestion = {
        val rng = new Random
        topic match {
          case "loops" => () => new Loops(rng.nextLong())
        }
      }
      val variantCount = variants.toInt
      val questionCount = questions.toInt

      val q = new PrintStream("q")
      try {
        val a = new PrintStream("a")
        try {
          for (variant <- 1 to variantCount) {
            q.println("Вариант " + variant)
            a.println("Вариант " + variant)
            for (question <- 1 to questionCount) {
              var done = false
              while (!done) {
                val currentQuestion = nextQuestion()
                answer(currentQuestion) match {
                  case Some(answer) => {
                    q.println(question + ".")
                    val qw = new DefaultWriter(q)
                    currentQuestion.question(qw)
                    qw.nextLine()
                    if (question != questionCount) {
                      qw.nextLine()
                    }

                    a.println(question + ". " + answer)

                    done = true
                  }
                  case None => {}
                }
              }
            }
            q.println("--------------------------------------------------------------------------------")
            a.println()
          }
        } finally {
          a.close()
        }
      } finally {
        q.close()
      }
  }

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