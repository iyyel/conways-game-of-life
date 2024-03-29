package io.iyyel.game.of.life.controls

import org.scalajs.dom

import scala.scalajs.js

final class Slider(input: dom.html.Input):
  def init(
      min: Int,
      max: Int,
      value: Int,
      disabled: Boolean = false
  ): Unit =
    input.min = min.toString
    input.max = max.toString
    input.value = value.toString
    input.disabled = disabled

  def max: Int = input.max.toInt
  def max_=(max: Int): Unit = input.max = max.toString

  def min: Int = input.min.toInt
  def min_=(min: Int): Unit = input.min = min.toString

  def value: Int = input.value.toInt
  def value_=(value: Int): Unit = input.value = value.toString

  def disabled: Boolean = input.disabled
  def disabled_=(disabled: Boolean): Unit = input.disabled = disabled

  def onSlide(handler: js.Function1[dom.UIEvent, js.Dynamic]): Unit =
    input.onactivate = handler
