package org.mephi.cquiz

import util.Random
import collection.mutable.ListBuffer

object Loops extends Topic {
  override val id = "loops"

  override val titleKey = "loopsTitle"

  override val descriptionKey = "loopsDescription"

  override def question(_seed: Long) = new SimpleCOutput {
    protected override def write(code: Writer) {
      rnd.setSeed(_seed)

      code.write("int i, j, x;").nextLine()
      code.write("x = ").write(rnd.nextInt(10)).write(";").nextLine()
      val nested = rnd.nextBoolean()
      if (nested) {
        loop("i", code) {
          loop("j", code) {
            code.write("x++;").nextLine()
          }
        }
      } else {
        loop("i", code) {
          code.write("x++;").nextLine()
        }
        loop("j", code) {
          code.write("x++;").nextLine()
        }
      }
      code.write("printf(\"%i\\n\", x);").nextLine()
    }

    override val minSteps = 10

    abstract class Relation(val text: String) {
      def opposite: Relation

      override def toString = text
    }

    object Less extends Relation("<") {
      override def opposite = Greater
    }

    object Greater extends Relation(">") {
      override def opposite = Less
    }

    object LessOrEqual extends Relation("<=") {
      override def opposite = GreaterOrEqual
    }

    object GreaterOrEqual extends Relation(">=") {
      override def opposite = LessOrEqual
    }

    object Equal extends Relation("==") {
      override def opposite = Equal
    }

    object NotEqual extends Relation("!=") {
      override def opposite = NotEqual
    }

    private def loop(counter: String, code: Writer)(body: => Any) {
      val start = rnd.nextInt(10)
      val end = rnd.nextInt(10)
      val delta = 1 + rnd.nextInt(3)
      val deltaIsPositive = if (end >= start) true else false

      val loopCondition = {
        val relations = {
          val builder = new ListBuffer[Relation]
          if (end >= start) {
            builder ++= Seq(Less, LessOrEqual)
          } else {
            builder ++= Seq(Greater, GreaterOrEqual)
          }
          if ((end - start) % delta == 0) {
            builder ++= Seq(Equal, NotEqual)
          }
          builder.toList
        }
        val relation = relations(rnd.nextInt(relations.size))
        if (rnd.nextBoolean())
          counter + " " + relation + " " + end
        else
          end + " " + relation.opposite + " " + counter
      }
      val initCounter = counter + " = " + start
      val nextCounter = {
        if (deltaIsPositive) {
          counter + " += " + delta
        } else {
          counter + " -= " + delta
        }
      }

      rnd.nextInt(3) match {
        case 0 => {
          code.write(initCounter).write(";").nextLine()
          code.write("while (").write(loopCondition).write(")").block {
            body
            code.write(nextCounter).write(";").nextLine()
          }.nextLine()
        }
        case 1 => {
          code.write(initCounter).write(";").nextLine()
          code.write("do").block {
            body
            code.write(nextCounter).write(";").nextLine()
          }.write(" while (").write(loopCondition).write(");").nextLine()
        }
        case 2 => {
          code.write("for (").write(initCounter).write("; ").write(loopCondition).write("; ").write(nextCounter).write(")").block {
            body
          }.nextLine()
        }
      }
    }

    private val rnd = new Random
  }
}
