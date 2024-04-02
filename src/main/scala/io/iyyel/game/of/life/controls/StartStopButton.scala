package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Span}

final class StartStopButton(
    val btnElement: Button,
    val runningState: State[Boolean]
):
  val clickState: State[Boolean] = State(false)

  private val label = btnElement
    .getElementsByClassName("btn-label")(0)
    .asInstanceOf[Span]

  private val symbol = btnElement
    .getElementsByClassName("btn-symbol")(0)
    .asInstanceOf[Span]

  btnElement.onclick = (* : dom.Event) => clickState.update(click => !click)

  runningState.observe({ running =>
    if running then
      label.textContent = "Stop"
      // symbol.classList.remove("glyphicon-play")
      // symbol.classList.add("glyphicon-stop")
    else
      label.textContent = "Start"
      // symbol.classList.remove("glyphicon-stop")
      // symbol.classList.add("glyphicon-play")
  })
