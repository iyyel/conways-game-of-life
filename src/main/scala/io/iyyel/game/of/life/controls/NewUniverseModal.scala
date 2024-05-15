package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.controls.NewUniverseModal.NewUniverseParams
import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import org.scalajs.dom
import org.scalajs.dom.html.*

import scala.scalajs.js

final class NewUniverseModal(
    rootElement: Div,
    universeSizeState: State[Int]
):
  val newUniverseParamsState: State[NewUniverseParams] =
    State(NewUniverseParams(-1, false))

  private val slider =
    Slider(rootElement.getChild[Input]("slider-new-universe-size"))
  slider.init(35, 10, 150)

  private val spanZoom =
    rootElement.getChild[Span]("span-new-universe-size")
  private val btnCreate =
    rootElement.getChild[Button]("btn-create-universe")
  private val btnRandom =
    rootElement.getChild[Input]("cb-new-universe-random-seed")

  slider.onSlide(_ =>
    val size = slider.value
    spanZoom.textContent = size.toString
    true.asInstanceOf[js.Dynamic]
  )

  btnCreate.onclick = _ =>
    dom.document.location.href = "#"
    val size = slider.value
    val random = btnRandom.checked
    newUniverseParamsState.set(NewUniverseParams(size, random))

  universeSizeState.observe(slider.value = _)

object NewUniverseModal:
  case class NewUniverseParams(size: Int, random: Boolean)
