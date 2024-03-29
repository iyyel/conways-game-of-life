package io.iyyel.game.of.life

import io.iyyel.game.of.life.controls.{StartStopButton, UniverseView, ZoomControl}
import io.iyyel.game.of.life.logic.{State, Universe, UniverseChanges}
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Canvas, Input}

import scala.concurrent.duration.DurationInt
import scala.scalajs.js.timers
import scala.scalajs.js.timers.SetTimeoutHandle

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

  def setTimeoutOnNextUniverse(): SetTimeoutHandle =
    val interval = 10.millis
    timers.setTimeout(interval) {
      if runningState.now() then
        val newGen = universeState.now().nextGeneration()
        universeState.update(_ => newGen._1)
        universeChangesState.update(_ => newGen._2)
        setTimeoutOnNextUniverse()
    }
  val timeout = setTimeoutOnNextUniverse()

  val startStopButton = StartStopButton(
    dom.document.getElementById("btn-start-stop").asInstanceOf[Button],
    runningState
  )

  val clearButton = controls.Button(
    dom.document.getElementById("btn-clear").asInstanceOf[Button]
  )

  val zoomControl = ZoomControl(
    dom.document.getElementById("zoom-slider").asInstanceOf[Input]
  )

  val universeView = UniverseView(
    dom.document.getElementById("canvas").asInstanceOf[Canvas],
    runningState,
    universeState,
    universeChangesState,
    zoomControl.zoomState
  )

  runningState.observeAfter(
    if _ then setTimeoutOnNextUniverse()
    else timers.clearTimeout(timeout)
  )

  universeView.cellPlaneClickState.observeAfter(cellCoords =>
    if !runningState.now() then universeState.update(_.flipCell(cellCoords))
  )

  startStopButton.clickState.observeAfter(_ =>
    runningState.update(running => !running)
  )

  clearButton.clickState.observeAfter(_ =>
    universeState.update(universe => universe.clear())
    runningState.set(false)
  )
