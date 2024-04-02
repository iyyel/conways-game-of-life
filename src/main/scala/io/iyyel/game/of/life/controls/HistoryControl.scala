package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.UniverseWithEpoch
import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import org.scalajs.dom.html.{Div, Input, Span}

import scala.scalajs.js

final class HistoryControl(
    val rootElement: Div,
    val universeEpochsState: State[List[UniverseWithEpoch]],
    val runningState: State[Boolean]
):
  private val slider = Slider(rootElement.getChild[Input]("slider-history"))
  slider.init(0, 1, 200, true)

  private val spanTimeLength = rootElement.getChild[Span]("span-time-length")

  val selectionState: State[Int] = State[Int](slider.value)

  private val disabledState =
    State[Boolean](runningState.now() || universeEpochsState.now().isEmpty)

  disabledState.observe(slider.disabled = _)

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
