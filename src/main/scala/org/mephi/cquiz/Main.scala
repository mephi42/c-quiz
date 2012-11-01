package org.mephi.cquiz

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
}