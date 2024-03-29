package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import org.scalajs.dom
import org.scalajs.dom.html.Input

import scala.scalajs.js

final class ZoomControl(rootElement: dom.html.Input):
  private val slider = Slider(rootElement)
  slider.init(1, 7, 3)

  val zoomState: State[Double] = State(1)

  slider.onSlide((* : dom.UIEvent) => {
    dom.console.log("ahhhh")
    // val zoomValue = getZoom(10)

    // jqSpanZoom.text(
    //  String.format("%0.2f×", java.lang.Double.valueOf(zoomValue))
    // )
    // zoomStream.update(_ => zoomValue)
    true.asInstanceOf[js.Dynamic]
  })

  private def getZoom(value: Int): Double =
    value match
      case 1 => 0.50
      case 2 => 0.75
      case 3 => 1.00
      case 4 => 1.25
      case 5 => 1.50
      case 6 => 1.75
      case 7 => 2.00
