package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import org.scalajs.dom.html.{Div, Input, Span}

import scala.scalajs.js

final class SpeedControl(rootElement: Div):
  private val slider = Slider(rootElement.getChild[Input]("slider-speed"))
  slider.init(6, 1, 8)

  private val spanSpeed: Span =
    rootElement.getChild[Span]("span-speed")

  val speedState: State[Int] = State(slider.value)

  slider.onSlide(_ =>
    spanSpeed.textContent = slider.value.toString
    speedState.set(slider.value)
    true.asInstanceOf[js.Dynamic]
  )
