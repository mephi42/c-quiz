package org.mephi.cquiz

import java.io.PrintStream

object Main extends App {
  args.toList match {
    case variants :: topics =>
      val quiz = makeQuiz(variants.toInt, topics)

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
              val code = new DefaultWriter(q)
              task.question.writeCode(code)
              code.nextLine()
              if (taskNumber != variant.length) {
                code.nextLine()
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
    case _ => sys.error("Unexpected arguments")
  }

  def forever(code: => Any): Nothing = {
    while (true) {
      code
    }
    sys.error("It is not possible to get here")
  }

  case class Task(question: Question, answer: String)

  def makeTask(generateQuestion: () => Question): Task = {
    forever {
      val question = generateQuestion()
      question.answer match {
        case Some(x) => return Task(question, x)
        case None => {}
      }
    }
  }

  def makeTask(topic: Topic): Task = PredefinedQuestions.questions match {
    case Some(s) => {
      val topicQuestions = s.getOrElse(topic.id, sys.error("No questions for topic " + topic.id))
      val question = topicQuestions(Rng.nextInt(topicQuestions.length))
      Task(question, question._answer)
    }
    case None => makeTask(() => topic.nextQuestion())
  }

  def makeQuiz(variantCount: Int, topicSpecs: Seq[String]): Array[Array[Task]] = {
    val topics = topicSpecs.flatMap(topicSpec => {
      topicSpec.split(":") match {
        case Array(id, count) =>
          val topic = Topics(id)
          Array.fill(count.toInt)(topic)
      }
    })
    (1 to variantCount).par.map(_ => {
      topics.par.map(topic => {
        makeTask(topic)
      }).toArray
    }).toArray
  }
}