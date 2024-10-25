package io.iyyel.game.of.life

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.scalajs.js.timers.SetTimeoutHandle
import scala.scalajs.js.timers

import io.iyyel.game.of.life.controls.NewUniverseModal.NewUniverseParams
import io.iyyel.game.of.life.extensions.Extensions.getElement
import io.iyyel.game.of.life.controls.*
import io.iyyel.game.of.life.logic.{
  State,
  Universe,
  UniverseChanges,
  Universes,
  UniverseWithEpoch
}

import org.scalajs.dom.html.{Button, Div}
import org.scalajs.dom

@main
def main(): Unit =
  val runningState: State[Boolean] =
    State(false)

  val universeSizeState: State[Int] =
    State(35)

  val universeChangesState: State[UniverseChanges] =
    State(null)

  val universeState: State[Universe] =
    State(Universes.TWO_GLIDERS)

  val currentEpochState: State[Int] =
    State[Int](1)

  val universeEpochsState: State[List[UniverseWithEpoch]] =
    State(List(UniverseWithEpoch(universeState.now(), currentEpochState.now())))

  val clearButton =
    UIButton(dom.document.getElement[Button]("btn-clear"))

  val randomUniverseButton =
    UIButton(dom.document.getElement[Button]("btn-random"))

  val speedControl =
    SpeedControl(dom.document.getElement[Div]("control-speed"))

  val zoomControl =
    ZoomControl(dom.document.getElement[Div]("control-zoom"))

  val startStopButton = StartStopButton(
    dom.document.getElement[Button]("btn-start-stop"),
    runningState
  )

  val historyControl = HistoryControl(
    dom.document.getElement[Div]("control-history"),
    universeEpochsState,
    runningState
  )

  val newUniverseModal = NewUniverseModal(
    dom.document.getElement[Div]("modal-new-universe"),
    universeSizeState
  )

  val predefinedUniverseModal = PredefinedUniverseModal(
    dom.document.getElement[Div]("modal-predefined-universe-content"),
    runningState,
    universeState,
    currentEpochState,
    universeEpochsState
  )

  val universeView = UniverseView(
    dom.document.getElement[Div]("universe"),
    runningState,
    universeState,
    universeChangesState,
    zoomControl.zoomState
  )

  universeView.cellPlaneClickState.observeAfter(cellCoords =>
    if !runningState.now() then
      universeState.update(_.flipCell(cellCoords))
      val newEpoch = 1
      currentEpochState.set(newEpoch)
      universeEpochsState.set(
        List(UniverseWithEpoch(universeState.now(), newEpoch))
      )
  )

  startStopButton.clickState.observeAfter(_ =>
    runningState.update(running => !running)
  )

  clearButton.clickState.observeAfter(_ =>
    startNewHistory(universeState.now().clear())
  )

  randomUniverseButton.clickState.observeAfter(_ =>
    startNewHistory(
      Universe.random(universeSizeState.now(), universeSizeState.now())
    )
  )

  newUniverseModal.newUniverseParamsState.observeAfter(_ =>
    val NewUniverseParams(size, random) =
      newUniverseModal.newUniverseParamsState.now()
    universeSizeState.set(size)
    val newUniverse =
      if random then Universe.random(size, size)
      else Universe(size, size)
    startNewHistory(newUniverse)
  )

  historyControl.selectionState.observeAfter(_ =>
    if !runningState.now() then
      val UniverseWithEpoch(universe, epoch) = universeEpochsState
        .now()
        .takeRight(historyControl.selectionState.now())
        .head
      universeState.set(universe)
      currentEpochState.set(epoch)
  )

  def startNewHistory(universe: Universe): Unit =
    runningState.set(false)
    universeState.set(universe)
    val newEpoch = 1
    currentEpochState.set(newEpoch)
    universeEpochsState.set(
      List(UniverseWithEpoch(universeState.now(), newEpoch))
    )

  def speedToDuration(speed: Int): FiniteDuration =
    speed match
      case 1 => 800.millis
      case 2 => 400.millis
      case 3 => 200.millis
      case 4 => 100.millis
      case 5 => 50.millis
      case 6 => 25.millis
      case 7 => 12.millis

  def setTimeoutOnNextUniverse(): SetTimeoutHandle =
    val interval = speedToDuration(speedControl.speedState.now())
    timers.setTimeout(interval):
      if runningState.now() then
        val newGen = universeState.now().nextGeneration()
        universeState.set(newGen._1)
        universeChangesState.set(newGen._2)
        val newEpoch =
          universeEpochsState.now().headOption.map(_.epoch).getOrElse(0) + 1
        currentEpochState.set(newEpoch)
        universeEpochsState.update(UniverseWithEpoch(newGen._1, newEpoch) :: _)
        setTimeoutOnNextUniverse()

  val timeout = setTimeoutOnNextUniverse()

  runningState.observeAfter(
    if _ then
      val lastEpoch = universeEpochsState.now().head.epoch
      val toDrop = lastEpoch - currentEpochState.now()
      universeEpochsState.update(_.drop(toDrop))
      setTimeoutOnNextUniverse()
    else timers.clearTimeout(timeout)
  )
