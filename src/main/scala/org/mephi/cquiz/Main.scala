package org.mephi.cquiz

import java.nio.file.Files
import java.io.{PrintStream, File}
import scala.sys.process._
import io.Source
import org.apache.commons.io.FileUtils

object Main extends App {
  args match {
    case Array(topic, variants, questions) =>
      val quiz = makeQuiz(topic, variants.toInt, questions.toInt)

      val q = new PrintStream("q")
      try {
        val a = new PrintStream("a")
        try {
          for (variantNumber <- 1 to quiz.length) {
            q.println("Вариант " + variantNumber)
            a.println("Вариант " + variantNumber)
            val variant = quiz(variantNumber - 1)
            for (taskNumber <- 1 to variant.length) {
              val task = variant(taskNumber - 1)

              q.println(taskNumber + ".")
              val qw = new DefaultWriter(q)
              task.question.write(qw)
              qw.nextLine()
              if (taskNumber != variant.length) {
                qw.nextLine()
              }

              a.println(taskNumber + ". " + task.answer)
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

  case class Task(question: Question, answer: String)

  def makeTask(question: Question) = answer(question).map(Task(question, _))

  def forever(code: => Any): Nothing = {
    while (true) {
      code
    }
    sys.error("It is not possible to get here")
  }

  def makeTask(gen: () => Question): Task = {
    forever {
      makeTask(gen()) match {
        case Some(task) => return task
        case None => {}
      }
    }
  }

  def makeTask(topic: Topic): Task = PredefinedSeeds.seeds match {
    case Some(s) => {
      val tasks = s.getOrElse(topic.id, sys.error("No seeds for " + topic.id))
      val (seed, answer) = tasks(Rng.nextInt(tasks.length))
      Task(topic.question(seed), answer)
    }
    case None => makeTask(() => topic.nextQuestion())
  }

  def makeQuiz(topicId: String, variantCount: Int, questionCount: Int): Array[Array[Task]] = {
    val topic = Topics(topicId)
    (1 to variantCount).par.map(_ => {
      (1 to questionCount).par.map(_ => {
        makeTask(topic)
      }).toArray
    }).toArray
  }

  def program(question: Question, writer: Writer) {
    writer.write("#include <stdio.h>").nextLine()
    for (include <- question.includes) {
      writer.write("#include ").write(include).nextLine()
    }
    writer.write("int main() ").block {
      question.write(writer)
      writer.write("return 0;").nextLine()
    }.nextLine().nextLine()
  }

  implicit def richProcessBuilder(pb: ProcessBuilder) = new {
    def !!!() {
      val code = pb.!
      if (code != 0) sys.error("Nonzero exit value: " + code)
    }
  }

  def answer(question: Question): Option[String] = {
    val dir = Files.createTempDirectory("c-quiz").toFile
    try {
      answer(question, dir)
    } finally {
      FileUtils.deleteDirectory(dir)
    }
  }

  private def answer(question: Question, dir: File): Option[String] = {
    val src = new File(dir, "c-quiz.c")
    val srcStream = new PrintStream(src)
    try {
      program(question, new DefaultWriter(srcStream))
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
        """.stripMargin.format(out, question.minSteps, question.maxSteps))
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