package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Span}
import io.iyyel.game.of.life.util.Extensions.{addClass, removeClass}

final class StartStopButton(
    val btnElement: Button,
    val runningState: State[Boolean]
):
  val clickState: State[Boolean] = State(false)

  private val label = btnElement
    .getElementsByClassName("btn-label")(0)
    .asInstanceOf[Span]

  private val symbol = btnElement
    .getElementsByClassName("btn-icon")(0)
    .asInstanceOf[Span]

  btnElement.onclick = (* : dom.Event) => clickState.update(click => !click)

  runningState.observe({ running =>
    if running then
      label.textContent = "Stop simulation"
      btnElement.addClass("stop-button")
      btnElement.removeClass("start-button")
      symbol.addClass("stop-icon")
      symbol.removeClass("start-icon")
    else
      label.textContent = "Start simulation"
      btnElement.addClass("start-button")
      btnElement.removeClass("stop-button")
      symbol.addClass("start-icon")
      symbol.removeClass("stop-icon")
  })
