package org.mephi.cquiz


class DefaultWriter(val app: Appendable) extends Writer {
  override def write(o: Any): Writer = {
    if (nextLinePending) {
      app.append("\n")
      app.append(indent)
      nextLinePending = false
    }
    app.append(String.valueOf(o))
    this
  }

  override def nextLine(): Writer = {
    if (nextLinePending) {
      app.append("\n")
    } else {
      nextLinePending = true
    }
    this
  }

  override def block(body: => Any) = {
    write("{")
    indent += "  "
    nextLine()
    body
    indent = indent.substring(2)
    write("}")
  }

  private var indent = ""
  private var nextLinePending = false
}
