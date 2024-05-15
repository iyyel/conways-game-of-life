package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import org.scalajs.dom.html.{Div, Input, Span}

import scala.scalajs.js

final class SpeedControl(rootElement: Div):
  private val slider = Slider(rootElement.getChild[Input]("slider-speed"))
  slider.init(4, 1, 7)

  private val spanSpeed: Span =
    rootElement.getChild[Span]("span-speed")

  val speedState: State[Int] = State(slider.value)

  slider.onSlide(_ =>
    val speedValue = speedToTimes(slider.value)
    spanSpeed.textContent = f"$speedValue%.2f×"
    speedState.set(slider.value)
    true.asInstanceOf[js.Dynamic]
  )

  def speedToTimes(speed: Int): Double =
    speed match
      case 1 => 0.10
      case 2 => 0.25
      case 3 => 0.50
      case 4 => 1.00
      case 5 => 2.00
      case 6 => 4.00
      case 7 => 8.00
