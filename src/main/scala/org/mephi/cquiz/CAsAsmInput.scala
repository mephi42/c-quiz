package org.mephi.cquiz

import java.io.StringWriter
import scala.sys.process.{ProcessLogger, Process}
import scala.collection.mutable.ListBuffer

/** Question: what do we need to feed this C program as input to get desired output?
  * C code is not shown, only relevant portions of assembly. */
trait CAsAsmInput extends Question {
  final override def writeCode(code: Writer) {
    code.write(data.asm)
    writeAsmComments(code, data.output)
  }

  final override lazy val answer = data.output.map(_ => makeAnswer(data.input))

  protected def writeInput(input: Writer)

  protected def writeCode(code: Writer, result: Option[String])

  protected def writeAsmComments(code: Writer, result: Option[String])

  protected def makeAnswer(input: String) = input

  protected def intelSyntax = false

  protected def functions: Set[String] = Set()

  protected def sections: Set[String] = Set()

  case class Data(asm: String, input: String, output: Option[String])

  private class Objdump {
    def onOutLine(line: String) {
      val functionMatcher = function.pattern.matcher(line)
      if (functionMatcher.matches()) {
        echo = functions.contains(functionMatcher.group(1))
      }
      val sectionMatcher = section.pattern.matcher(line)
      if (sectionMatcher.matches()) {
        echo = sections.contains(sectionMatcher.group(1))
      }
      if (echo) {
        text.append(line).append("\n")
      }
    }

    val text = new StringBuilder
    private var echo = false
    private val function = """[0-9a-f]+ <(.*)>:""".r
    private val section = """Contents of section (.*):""".r
  }

  private lazy val data: Data = {
    val runner = new GdbRunner()
    val (output, asm) = try {
      val output = runner.result((x, y) => {
        writeCode(x, None)
        writeInput(y)
      }, minSteps, maxSteps)

      val command = new ListBuffer[String]
      command ++= Seq("objdump", "-d", "-s")
      if (intelSyntax) {
        command ++= Seq("-M", "intel-mnemonic")
      }
      command += runner.bin.toString
      val objdump = new Objdump
      val logger = ProcessLogger(objdump.onOutLine, (errLine) => {
        Console.err.println(errLine)
      })
      val objdumpExitValue = Process(command.toSeq).run(logger).exitValue()
      if (objdumpExitValue != 0) {
        sys.error("objdump failed")
      }

      (output, objdump.text.toString())
    } finally {
      runner.close()
    }
    val input = new StringWriter()
    writeInput(new DefaultWriter(input))
    Data(asm, input.toString, output)
  }
}
