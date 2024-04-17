package io.iyyel.game.of.life.controls

import org.scalajs.dom
import org.scalajs.dom.html.Input

import scala.scalajs.js

final class Slider(input: Input):
  def init(
      value: Int,
      min: Int,
      max: Int,
      disabled: Boolean = false
  ): Unit =
    input.value = value.toString
    input.min = min.toString
    input.max = max.toString
    input.disabled = disabled

  def value: Int = input.value.toInt
  def value_=(value: Int): Unit = input.value = value.toString

  def max: Int = input.max.toInt
  def max_=(max: Int): Unit = input.max = max.toString

  def min: Int = input.min.toInt
  def min_=(min: Int): Unit = input.min = min.toString

  def disabled: Boolean = input.disabled
  def disabled_=(disabled: Boolean): Unit = input.disabled = disabled

  def onSlide(handler: js.Function1[dom.Event, js.Dynamic]): Unit =
    input.oninput = handler
