package io.iyyel.game.of.life.util

import org.scalajs.dom.{Element, HTMLElement, html}

object Extensions:

  extension (document: html.Document)
    def getElement[A](elementId: String): A =
      document.getElementById(elementId).asInstanceOf[A]

  extension (rootElement: HTMLElement)
    def getChild[A](childId: String): A =
      def getChildHelper(
          elements: Seq[Element],
          childId: String
      ): Option[Element] =
        elements.flatMap {
          case element if element.id == childId => Some(element)
          case element if element.children.nonEmpty =>
            getChildHelper(element.children.toSeq, childId)
          case _ => None
        }.headOption
      getChildHelper(Seq(rootElement), childId)
        .getOrElse(
          throw new NoSuchElementException(s"Child with ID $childId not found")
        )
        .asInstanceOf[A]

    def addClass(className: String): Unit =
      rootElement.className += s" $className"
      rootElement.classList.add(className)

    def removeClass(className: String): Unit =
      rootElement.className = rootElement.className.replace(s" $className", "")
      rootElement.classList.remove(className)
