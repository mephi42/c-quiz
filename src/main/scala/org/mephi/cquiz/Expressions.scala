package org.mephi.cquiz


import util.Random
import collection.mutable

object Expressions extends Topic {
  override val id = "expressions"

  override val titleKey = "expressionsTitle"

  override val descriptionKey = "expressionsDescription"

  override def question(_seed: Long) = new SimpleCOutput {
    protected override def write(code: Writer) {
      val gen = new Gen(_seed)
      if (!gen.vars.isEmpty) {
        code.write("int ").write(gen.vars.mkString(", ")).write(";").nextLine()
      }
      code.write( """printf("%i\n", """).write(gen.expr).write(");").nextLine()
    }
  }

  class Gen(seed: Long) {
    private val rnd = new Random(seed)
    private val choices0 = Seq(() => mkLiteral())
    private val choicesN = Seq(() => mkTernary(), () => mkComma(), () => mkAssign(), () => mkEqual(), () => mkNotEqual(),
      () => mkGreater(), () => mkLess(), () => mkGreaterOrEqual(), () => mkLessOrEqual(), () => mkLogicalAnd(), () => mkLogicalOr())
    private val names = "abcdefghijklmnopqrstuvwxyz"
    private var depth = 2
    private val variables = new mutable.HashSet[String]
    private val expression = mkExpr()

    def vars = variables.toSeq

    def expr = expression

    def mkExpr(): String = {
      val choices = if (depth == 0) choices0 else choicesN
      val func = choices(rnd.nextInt(choices.size))
      depth -= 1
      val result = func()
      depth += 1
      result
    }

    private def variable() = {
      val index = rnd.nextInt(names.length)
      val name = names.substring(index, index + 1)
      variables += name
      name
    }

    private def mkLiteral() = if (rnd.nextInt(3) == 0) "0" else rnd.nextInt(20).toString

    private def mkTernary() = "(" + mkExpr() + " ? " + mkExpr() + " : " + mkExpr() + ")"

    private def mkComma() = "(" + mkExpr() + ", " + mkExpr() + ")"

    private def mkAssign() = "(" + variable() + " = " + mkExpr() + ")"

    private def mkEqual() = "(" + mkExpr() + " == " + mkExpr() + ")"

    private def mkNotEqual() = "(" + mkExpr() + " != " + mkExpr() + ")"

    private def mkGreater() = "(" + mkExpr() + " > " + mkExpr() + ")"

    private def mkLess() = "(" + mkExpr() + " < " + mkExpr() + ")"

    private def mkGreaterOrEqual() = "(" + mkExpr() + " >= " + mkExpr() + ")"

    private def mkLessOrEqual() = "(" + mkExpr() + " <= " + mkExpr() + ")"

    private def mkLogicalAnd() = "(" + mkExpr() + " && " + mkExpr() + ")"

    private def mkLogicalOr() = "(" + mkExpr() + " || " + mkExpr() + ")"
  }

}
