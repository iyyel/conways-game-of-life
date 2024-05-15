package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.UniverseWithEpoch
import io.iyyel.game.of.life.util.Extensions.getChild
import io.iyyel.game.of.life.logic.State

import org.scalajs.dom.html.{Div, Input, Span}
import org.scalajs.dom
import scala.scalajs.js

final class HistoryControl(
    val rootElement: Div,
    val universeEpochsState: State[List[UniverseWithEpoch]],
    val runningState: State[Boolean]
):
  private val slider = Slider(rootElement.getChild[Input]("slider-history"))
  slider.init(0, 1, 0, true)

  private val spanTimeLength = rootElement.getChild[Span]("span-time-length")

  val selectionState: State[Int] = State[Int](slider.value)

  runningState.observeAfterWith(
    universeEpochsState,
    (running, universeEpochs) =>
      slider.disabled = running || universeEpochs.size <= 1
  )

  universeEpochsState.observe(universeEpochs =>
    val lastEpoch = universeEpochs.headOption.map(_.epoch).getOrElse(0)
    slider.max = lastEpoch
    slider.value = lastEpoch
    spanTimeLength.textContent = lastEpoch.toString
  )

  slider.onSlide(_ =>
    val sliderValue = slider.value
    selectionState.set(sliderValue)
    spanTimeLength.textContent = sliderValue.toString
    true.asInstanceOf[js.Dynamic]
  )
