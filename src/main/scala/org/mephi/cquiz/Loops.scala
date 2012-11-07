package org.mephi.cquiz

import util.Random

object Loops extends Topic {
  override val id = "loops"

  override val titleKey = "loopsTitle"

  override val descriptionKey = "loopsDescription"

  override def question(_seed: Long) = new Question {
    override def write(writer: Writer) {
      rnd.setSeed(seed)

      writer.write("int i, j, x;").nextLine()
      writer.write("x = ").write(rnd.nextInt(10)).write(";").nextLine()
      val nested = rnd.nextBoolean()
      if (nested) {
        loop("i", writer) {
          loop("j", writer) {
            writer.write("x++;").nextLine()
          }
        }
      } else {
        loop("i", writer) {
          writer.write("x++;").nextLine()
        }
        loop("j", writer) {
          writer.write("x++;").nextLine()
        }
      }
      writer.write("printf(\"%i\\n\", x);").nextLine()
    }

    override def seed = _seed

    override val minSteps = 15

    private def loop(counter: String, writer: Writer)(body: => Any) {
      val start = rnd.nextInt(10)
      val end = rnd.nextInt(10)
      val delta = 1 + rnd.nextInt(3)

      val loopCondition = {
        val relation = rnd.nextInt(6) match {
          case 0 => "<"
          case 1 => "<="
          case 2 => ">"
          case 3 => ">="
          case 4 => "=="
          case 5 => "!="
        }
        if (rnd.nextBoolean())
          counter + " " + relation + " " + end
        else
          end + " " + relation + " " + counter
      }
      val initCounter = counter + " = " + start
      val nextCounter = {
        if (rnd.nextBoolean()) {
          counter + " += " + delta
        } else {
          counter + " -= " + delta
        }
      }

      rnd.nextInt(3) match {
        case 0 => {
          writer.write(initCounter).write(";").nextLine()
          writer.write("while (").write(loopCondition).write(") ").block {
            body
            writer.write(nextCounter).write(";").nextLine()
          }.nextLine()
        }
        case 1 => {
          writer.write(initCounter).write(";").nextLine()
          writer.write("do ").block {
            body
            writer.write(nextCounter).write(";").nextLine()
          }.write(" while (").write(loopCondition).write(");").nextLine()
        }
        case 2 => {
          writer.write("for (").write(initCounter).write("; ").write(loopCondition).write("; ").write(nextCounter).write(") ").block {
            body
          }.nextLine()
        }
      }
    }

    private val rnd = new Random
  }
}
