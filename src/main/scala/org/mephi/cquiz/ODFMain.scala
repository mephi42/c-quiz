package org.mephi.cquiz

import org.odftoolkit.odfdom.doc.OdfTextDocument
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily
import org.odftoolkit.odfdom.dom.style.props.{OdfPageLayoutProperties, OdfTextProperties}
import java.io.StringWriter

object ODFMain extends App {
  args.toList match {
    case variants :: topics => {
      val quiz = Main.makeQuiz(variants.toInt, topics)

      // Create an empty document.
      val odf = OdfTextDocument.newTextDocument()
      odf.getContentRoot.removeChild(odf.getContentRoot.getLastChild)

      // Configure page.
      val automaticStyles = odf.getStylesDom.getOrCreateAutomaticStyles
      val pageLayout = automaticStyles.getPageLayout("pm1")
      pageLayout.setProperty(OdfPageLayoutProperties.PrintOrientation, "landscape")
      pageLayout.setProperty(OdfPageLayoutProperties.PageHeight, "210.01mm")
      pageLayout.setProperty(OdfPageLayoutProperties.PageWidth, "297mm")
      pageLayout.setProperty(OdfPageLayoutProperties.NumFormat, "1")
      pageLayout.newStylePageLayoutPropertiesElement("").newStyleColumnsElement(2)

      // Configure styles.
      val styles = odf.getOrCreateDocumentStyles
      val codeStyle = styles.newStyle("Code", OdfStyleFamily.Paragraph)
      codeStyle.setProperty(OdfTextProperties.FontFamily, "courier")
      codeStyle.setProperty(OdfTextProperties.FontSize, "8pt")
      val normalStyle = styles.newStyle("Normal", OdfStyleFamily.Paragraph)

      // TODO: configure tabs
      // left 0.87"
      // left 2.39"

      // Questions.
      for (variantNumber <- 1 to quiz.length) {
        odf.newParagraph().setDocumentStyle(normalStyle)
        odf.addText("Вариант " + variantNumber)
        val variant = quiz(variantNumber - 1)
        for (taskNumber <- 1 to variant.length) {
          val task = variant(taskNumber - 1)
          if (variant.length > 1) {
            odf.newParagraph().setDocumentStyle(normalStyle)
            odf.addText(taskNumber + ".")
          }
          val code = new StringWriter()
          task.question.writeCode(new DefaultWriter(code))
          val codeParagraph = odf.newParagraph()
          codeParagraph.setDocumentStyle(codeStyle)
          for (line <- code.toString.split("\\n")) {
            var first = true
            for (tab <- line.split("\\t")) {
              if (first) {
                first = false
              } else {
                codeParagraph.newTextTabElement()
              }
              codeParagraph.newTextNode(tab)
            }
            codeParagraph.newTextLineBreakElement()
          }
        }
      }
      // TODO: page break

      // Answers.
      // TODO: set 3 columns
      val answerParagraph = odf.newParagraph()
      answerParagraph.setDocumentStyle(normalStyle)
      for (variantNumber <- 1 to quiz.length) {
        answerParagraph.newTextNode("Вариант " + variantNumber)
        answerParagraph.newTextLineBreakElement()
        val variant = quiz(variantNumber - 1)
        for (taskNumber <- 1 to variant.length) {
          val task = variant(taskNumber - 1)
          answerParagraph.newTextNode(taskNumber + ". " + task.answer)
          answerParagraph.newTextLineBreakElement()
        }
        answerParagraph.newTextLineBreakElement()
      }
      odf.save("c-quiz.odf")
    }
    case _ => sys.error("Unexpected arguments")
  }
}
