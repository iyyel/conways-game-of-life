package io.iyyel.game.of.life

import com.raquo.laminar.api.L.{*, given}

enum Cell:
  case Alive, Dead

type State = Array[Array[Cell]]

final class Model:
  private val stateVar: Var[State] = Var(Array.fill(20, 20)(Cell.Dead))
  val stateSignal: StrictSignal[State] = stateVar.signal

  val updateCell: (Int, Int, Cell) => Unit =
    (rowIndex: Int, colIndex: Int, cell: Cell) =>
      stateVar.update(state =>
        state(rowIndex)(colIndex) = cell
        state
      )

  val clearCells: () => Unit = () =>
    for {
      rowIndex <- stateSignal.now().indices
      colIndex <- stateSignal.now()(0).indices
    } yield {
      if stateSignal.now()(rowIndex)(colIndex) == Cell.Alive then
        updateCell(rowIndex, colIndex, Cell.Dead)
    }
