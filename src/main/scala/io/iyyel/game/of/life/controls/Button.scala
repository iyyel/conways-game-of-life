package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import org.scalajs.dom

final class Button(val btnElement: dom.html.Button):
  val clickState: State[Boolean] = State(false)
  btnElement.onclick = (* : dom.Event) => clickState.set(true)
