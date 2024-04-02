package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.State
import org.scalajs.dom
import org.scalajs.dom.html.Button

final class UIButton(val btnElement: Button):
  val clickState: State[Boolean] = State(false)
  btnElement.onclick = (* : dom.Event) => clickState.set(true)
