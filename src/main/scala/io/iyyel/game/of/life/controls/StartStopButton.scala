package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.util.Extensions.{addClass, removeClass}
import io.iyyel.game.of.life.util.Extensions.getChild
import io.iyyel.game.of.life.logic.State

import org.scalajs.dom.html.{Button, Span}
import org.scalajs.dom

final class StartStopButton(
    val btnElement: Button,
    val runningState: State[Boolean]
):
  val clickState: State[Boolean] = State(false)

  private val label =
    btnElement.getChild[Span]("btn-label")

  private val icon =
    btnElement.getChild[Span]("btn-icon")

  btnElement.onclick = (* : dom.Event) => clickState.update(click => !click)

  runningState.observe({ running =>
    if running then
      label.textContent = "Stop simulation"
      btnElement.addClass("stop-btn")
      btnElement.removeClass("start-btn")
      icon.addClass("stop-icon")
      icon.removeClass("start-icon")
    else
      label.textContent = "Start simulation"
      btnElement.addClass("start-btn")
      btnElement.removeClass("stop-btn")
      icon.addClass("start-icon")
      icon.removeClass("stop-icon")
  })
