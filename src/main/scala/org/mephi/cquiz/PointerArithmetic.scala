package org.mephi.cquiz

import util.Random

object PointerArithmetic extends Topic {
  override val id = "pointer-arithmetic"

  override val titleKey = "pointerArithmeticTitle"

  override val descriptionKey = "pointerArithmeticDescription"

  def question(_seed: Long) = new Question {
    override def write(writer: Writer) {
      val variables: Array[Char] = rng.shuffle(('a' to 'z').toSeq).toArray

      class Expr(val s: String, val weight: Int, val variables: Set[Char]) {
        def this(left: Expr, op: String, right: Expr) =
          this("(" + left + " " + op + " " + right + ")",
            left.weight + right.weight + 1,
            left.variables ++ right.variables)

        def this(i: Int) = this("0x%x".format(i), 0, Set[Char]())

        def this(variable: Char) = this(variable.toString, 0, Set(variable))

        override def toString = s
      }

      def mkInt(weight: Int) = {
        if (weight == 0) mkIntLiteral()
        else mkPtrMinusPtr(weight)
      }

      def mkIntLiteral() = new Expr(1 + rng.nextInt(15))

      def mkPtrType() = rng.nextInt(3)

      def mkPtrMinusPtr(weight: Int): Expr = {
        val ptrType = mkPtrType()
        val (leftWeight, rightWeight) = splitWeight(weight)
        val left = mkPtr(ptrType, leftWeight)
        Main.forever {
          val right = mkPtr(ptrType, rightWeight)
          if (left.s != right.s) {
            return new Expr(left, "-", right)
          }
        }
      }

      def mkPtr(ptrType: Int, weight: Int): Expr = {
        if (weight == 0) mkPtrLiteral(ptrType)
        else rng.nextInt(2) match {
          case 0 => mkPtrPlusN(ptrType, weight)
          case 1 => mkPtrMinusN(ptrType, weight)
        }
      }

      def mkPtrLiteral(ptrType: Int) = {
        val index = ptrType * 8 + rng.nextInt(8)
        val variable: Char = variables(index)
        new Expr(variable)
      }

      def mkPtrPlusN(ptrType: Int, weight: Int) = {
        val (leftWeight, rightWeight) = splitWeight(weight)
        new Expr(mkPtr(ptrType, leftWeight), "+", mkInt(rightWeight))
      }

      def mkPtrMinusN(ptrType: Int, weight: Int) = {
        val (leftWeight, rightWeight) = splitWeight(weight)
        new Expr(mkPtr(ptrType, leftWeight), "-", mkInt(rightWeight))
      }

      def splitWeight(weight: Int) = {
        if (weight == 1) (0, 0)
        else {
          val leftWeight = (weight - 1) / 2 // results look better than for rng.nextInt(weight - 1)
          (leftWeight, weight - leftWeight - 1)
        }
      }

      val weight = 3
      val expr = mkInt(weight)

      for (variable <- expr.variables) {
        val idx = variables.indexOf(variable)
        val tpe = "uint" + (8 << (idx / 8)) + "_t"
        writer.write("%s *%c = (%s*)0x%x;".format(tpe, variable, tpe, rng.nextInt(16))).nextLine()
      }

      writer.write( """printf("%%d\n", %s);""".format(expr)).nextLine()
    }

    override def seed = _seed

    override def includes = Seq("<stdint.h>")

    private val rng = new Random(seed)
  }
}
