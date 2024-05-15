package io.iyyel.game.of.life.logic

import scala.util.Random

import io.iyyel.game.of.life.logic.CellPlane.{
  Alive,
  CellCoordinates,
  Dead,
  Unchanged
}
import io.iyyel.game.of.life.*

final case class Universe private (
    cellPlane: CellPlane
) extends CellPlaneContainer:
  val width: Int = cellPlane.width
  val height: Int = cellPlane.height

  def setCellAlive(cellCoords: CellCoordinates): Universe =
    copy(cellPlane.setCellAlive(cellCoords))

  def setCellAlive(row: Int, col: Int): Universe =
    copy(cellPlane.setCellAlive(CellCoordinates(row, col)))

  def setCellDead(cellCoords: CellCoordinates): Universe =
    copy(cellPlane.setCellDead(cellCoords))

  def setCellDead(row: Int, col: Int): Universe =
    copy(cellPlane.setCellDead(CellCoordinates(row, col)))

  def flipCell(cellCoords: CellCoordinates): Universe =
    copy(cellPlane.flipCell(cellCoords))

  def flipCell(row: Int, col: Int): Universe =
    copy(cellPlane.flipCell(CellCoordinates(row, col)))

  def clear(): Universe =
    copy(cellPlane.clear())

  def nextGeneration(): (Universe, UniverseChanges) =
    val cellPlaneBuffer =
      CellPlaneBuffer(cellPlane.width, cellPlane.height, Dead)
    val universeChangesBuffer =
      CellPlaneBuffer(cellPlane.width, cellPlane.height, Unchanged)

    for
      row <- 0 until cellPlane.height
      col <- 0 until cellPlane.width
    yield
      val cellCoords = CellCoordinates(row, col)
      val aliveNeighborsCount = cellPlane.countAliveNeighbors(cellCoords)

      cellPlane.getCell(cellCoords) match
        case Alive =>
          if aliveNeighborsCount < 2 || aliveNeighborsCount > 3 then
            cellPlaneBuffer(cellCoords) = Dead
            universeChangesBuffer(cellCoords) = Dead
          else cellPlaneBuffer(cellCoords) = Alive
        case Dead =>
          if aliveNeighborsCount == 3 then
            cellPlaneBuffer(cellCoords) = Alive
            universeChangesBuffer(cellCoords) = Alive
          else cellPlaneBuffer(cellCoords) = Dead
        case _ =>
    (
      new Universe(cellPlaneBuffer.build()),
      UniverseChanges(universeChangesBuffer.build())
    )

  private def copy(cellPlane: CellPlane): Universe =
    new Universe(cellPlane)

object Universe:
  def apply(width: Int, height: Int): Universe =
    new Universe(CellPlane(width, height))

  def random(width: Int, height: Int): Universe =
    val cellPlaneBuffer = CellPlaneBuffer(width, height, CellPlane.Dead)

    for
      row <- 0 until height
      col <- 0 until width
    yield
      if Random.nextInt(4) == 1 then
        cellPlaneBuffer(CellCoordinates(row, col)) = Alive

    new Universe(cellPlaneBuffer.build())
