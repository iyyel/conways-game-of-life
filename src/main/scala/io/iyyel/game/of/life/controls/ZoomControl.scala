package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import org.scalajs.dom
import org.scalajs.dom.html.{Div, Input, Span}

import scala.scalajs.js

final class ZoomControl(rootElement: Div):
  private val slider = Slider(rootElement.getChild[Input]("slider-zoom"))
  slider.init(4, 1, 7)

  private val spanZoom = rootElement.getChild[Span]("span-zoom")

  val zoomState: State[Double] = State(getZoom(slider.value))

  slider.onSlide(_ =>
    val zoomValue = getZoom(slider.value)
    spanZoom.textContent = f"$zoomValue%.2f×"
    zoomState.set(zoomValue)
    true.asInstanceOf[js.Dynamic]
  )

  private def getZoom(value: Int): Double =
    value match
      case 1 => 0.25
      case 2 => 0.50
      case 3 => 0.75
      case 4 => 1.00
      case 5 => 1.25
      case 6 => 1.50
      case 7 => 1.75
