package io.iyyel.game.of.life

import io.iyyel.game.of.life.controls.*
import io.iyyel.game.of.life.controls.NewUniverseModal.NewUniverseParams
import io.iyyel.game.of.life.logic.{State, Universe, UniverseChanges}
import io.iyyel.game.of.life.util.Extensions.getElement
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Div}

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.scalajs.js.timers
import scala.scalajs.js.timers.SetTimeoutHandle

private[life] case class UniverseWithEpoch(universe: Universe, epoch: Int)

@main
def main(): Unit =
  val runningState: State[Boolean] = State(false)
  val universeSizeState: State[Int] = State(35)
  val universeChangesState: State[UniverseChanges] = State(null)
  val universeState: State[Universe] = State(
    Universe(universeSizeState.now(), universeSizeState.now())
      // Left glider
      .setCellAlive(0, 1)
      .setCellAlive(1, 2)
      .setCellAlive(2, 0)
      .setCellAlive(2, 1)
      .setCellAlive(2, 2)

      // Right glider
      .setCellAlive(0, 32)
      .setCellAlive(1, 31)
      .setCellAlive(2, 33)
      .setCellAlive(2, 32)
      .setCellAlive(2, 31)
  )
  val currentEpochState: State[Int] = State[Int](1)
  val universeEpochsState: State[List[UniverseWithEpoch]] =
    State(List(UniverseWithEpoch(universeState.now(), currentEpochState.now())))

  val speedControl =
    SpeedControl(dom.document.getElement[Div]("control-speed"))

  val clearButton =
    UIButton(dom.document.getElement[Button]("btn-clear"))

  val newUniverseModal = NewUniverseModal(
    dom.document.getElement[Div]("modal-new-universe"),
    universeSizeState
  )

  val randomUniverseButton =
    UIButton(dom.document.getElement[Button]("btn-random"))

  val zoomControl =
    ZoomControl(dom.document.getElement[Div]("control-zoom"))

  val universeView = UniverseView(
    dom.document.getElement[Div]("universe"),
    runningState,
    universeState,
    universeChangesState,
    zoomControl.zoomState
  )

  val startStopButton = StartStopButton(
    dom.document.getElement[Button]("btn-start-stop"),
    runningState
  )

  val historyControl = HistoryControl(
    dom.document.getElement[Div]("control-history"),
    universeEpochsState,
    runningState
  )

  def startNewHistory(universe: Universe): Unit =
    runningState.set(false)
    universeState.set(universe)
    currentEpochState.set(1)
    universeEpochsState.set(
      List(UniverseWithEpoch(universeState.now(), currentEpochState.now()))
    )

  universeView.cellPlaneClickState.observeAfter(cellCoords =>
    if !runningState.now() then
      universeState.update(_.flipCell(cellCoords))
      currentEpochState.set(1)
      universeEpochsState.set(
        List(UniverseWithEpoch(universeState.now(), currentEpochState.now()))
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

  def speedToDuration(speed: Int): FiniteDuration =
    speed match
      case 1 => 800.millis // 0.12×
      case 2 => 400.millis // 0.25×
      case 3 => 200.millis // 0.50×
      case 4 => 100.millis // 1.00×
      case 5 => 50.millis // 2.00×
      case 6 => 25.millis // 4.00×
      case 7 => 12.millis // 8.00×

  def setTimeoutOnNextUniverse(): SetTimeoutHandle =
    val interval = speedToDuration(speedControl.speedState.now())
    timers.setTimeout(interval) {
      if runningState.now() then
        val newGen = universeState.now().nextGeneration()
        universeState.set(newGen._1)
        universeChangesState.set(newGen._2)
        val newEpoch =
          universeEpochsState.now().headOption.map(_.epoch).getOrElse(0) + 1
        currentEpochState.set(newEpoch)
        universeEpochsState.update(UniverseWithEpoch(newGen._1, newEpoch) :: _)
        setTimeoutOnNextUniverse()
    }

  val timeout = setTimeoutOnNextUniverse()

  runningState.observeAfter(
    if _ then
      val lastEpoch = universeEpochsState.now().head.epoch
      val toDrop = lastEpoch - currentEpochState.now()
      universeEpochsState.update(_.drop(toDrop))
      setTimeoutOnNextUniverse()
    else timers.clearTimeout(timeout)
  )
