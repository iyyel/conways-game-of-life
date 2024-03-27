package io.iyyel.game.of.life

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.features.unitArrows
import org.scalajs.dom

@main
def Life(): Unit =
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )

object Main:
  private val model = new Model

  import model.*

  def appElement(): Element =
    div(
      renderHeaders(),
      renderState(model.stateSignal)
    )

  private val renderHeaders = () =>
    div(
      h1("Game of Life"),
      button("Clear cells", onClick --> model.clearCells()),
      button("Next", onClick --> evolve(model.stateSignal))
    )

  private val renderState = (state: StrictSignal[State]) =>
    table(
      tbody(
        children <-- state.map(_.toList.zipWithIndex.map {
          case (row, rowIndex) =>
            renderRow(row, rowIndex)
        })
      )
    )

  private val renderRow = (row: Array[Cell], rowIndex: Int) =>
    tr(
      row.toList.zipWithIndex.map:
        case (cell, colIndex) =>
          renderCell(rowIndex, colIndex, cell)
    )

  private val renderCell = (rowIndex: Int, colIndex: Int, cell: Cell) =>
    if cell == Cell.Alive then
      td(
        cls := ("cell", "alive"),
        onClick --> model.updateCell(rowIndex, colIndex, Cell.Dead)
      )
    else
      td(
        cls := ("cell", "dead"),
        onClick --> model.updateCell(rowIndex, colIndex, Cell.Alive)
      )

  private val aliveNeighbours: (Int, Int, State) => Int =
    (rowIndex: Int, colIndex: Int, state: State) =>
      val neighboursPositions = for {
        row <- rowIndex - 1 to rowIndex + 1
        column <- colIndex - 1 to colIndex + 1
        if row >= 0 && row <= state.length - 1
          && column >= 0 && column <= state(row).length - 1
      } yield (row, column)
      neighboursPositions
        .filterNot(_ == (rowIndex, colIndex))
        .map((row, column) => state(row)(column))
        .count(_ == Cell.Alive)

  private val evolve: StrictSignal[State] => Unit =
    (state: StrictSignal[State]) =>
      for {
        rowIndex <- stateSignal.now().indices
        colIndex <- stateSignal.now()(0).indices
      } yield {
        val cell = state.now()(rowIndex)(colIndex)
        val liveNeighbours = aliveNeighbours(rowIndex, colIndex, state.now())
        val newCell = if cell == Cell.Alive then {
          if liveNeighbours < 2 then Cell.Dead
          else if liveNeighbours == 2 || liveNeighbours == 3 then Cell.Alive
          else Cell.Dead
        } else {
          if liveNeighbours == 3 then Cell.Alive else Cell.Dead
        }
        model.updateCell(rowIndex, colIndex, newCell)
      }
