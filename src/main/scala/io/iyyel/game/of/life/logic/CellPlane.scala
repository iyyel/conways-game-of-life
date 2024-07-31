package io.iyyel.game.of.life.logic

import scala.collection.mutable.ArrayBuffer

import io.iyyel.game.of.life.logic.CellPlane.*

sealed trait CellPlaneBase:
  val width: Int
  val height: Int

  extension (cellCoords: CellCoordinates)
    protected def toPlaneCoordinates: Int =
      cellCoords.row * width + cellCoords.col

final case class CellPlane private[logic] (
    width: Int,
    height: Int,
    cells: Vector[Cell]
) extends CellPlaneBase:
  def setCellAlive(cellCoords: CellCoordinates): CellPlane =
    setCellAliveByPlaneCoordinates(cellCoords.toPlaneCoordinates)

  def setCellDead(cellCoords: CellCoordinates): CellPlane =
    setCellDeadByPlaneCoordinates(cellCoords.toPlaneCoordinates)

  def flipCell(cellCoords: CellCoordinates): CellPlane =
    val planeCoords = cellCoords.toPlaneCoordinates
    val cell = cells(planeCoords)
    cell match
      case Alive =>
        setCellDeadByPlaneCoordinates(planeCoords)
      case Dead =>
        setCellAliveByPlaneCoordinates(planeCoords)
      case _ =>
        this

  def getCell(cellCoords: CellCoordinates): Cell =
    cells(cellCoords.toPlaneCoordinates)

  def clear(): CellPlane =
    CellPlane(width, height)

  def countAliveNeighbors(cellCoords: CellCoordinates): Int =
    getCellNeighbors(cellCoords).neighbors.count: coords =>
      getCell(coords) == Alive

  private def getCellNeighbors(cellCoords: CellCoordinates): CellNeighbors =
    val CellCoordinates(row, col) = cellCoords
    val colLeft = if col > 0 then col - 1 else width - 1
    val colRight = if col < width - 1 then col + 1 else 0
    val rowUpper = if row > 0 then row - 1 else height - 1
    val rowLower = if row < height - 1 then row + 1 else 0

    CellNeighbors(
      CellCoordinates(rowUpper, colLeft),
      CellCoordinates(rowUpper, col),
      CellCoordinates(rowUpper, colRight),
      CellCoordinates(row, colRight),
      CellCoordinates(rowLower, colRight),
      CellCoordinates(rowLower, col),
      CellCoordinates(rowLower, colLeft),
      CellCoordinates(row, colLeft)
    )

  private def setCellAliveByPlaneCoordinates(planeCoords: Int): CellPlane =
    copy(cells.updated(planeCoords, Alive))

  private def setCellDeadByPlaneCoordinates(planeCoords: Int): CellPlane =
    copy(cells.updated(planeCoords, Dead))

  private def copy(cells: Vector[Cell]): CellPlane =
    new CellPlane(width, height, cells)

object CellPlane:
  sealed trait Cell
  case object Alive extends Cell
  case object Dead extends Cell
  case object Unchanged extends Cell

  final case class CellCoordinates(row: Int, col: Int)

  private final case class CellNeighbors(
      upperLeft: CellCoordinates,
      upper: CellCoordinates,
      upperRight: CellCoordinates,
      right: CellCoordinates,
      lowerRight: CellCoordinates,
      lower: CellCoordinates,
      lowerLeft: CellCoordinates,
      left: CellCoordinates
  ):
    val neighbors: List[CellCoordinates] =
      List(
        upperLeft,
        upper,
        upperRight,
        right,
        lowerRight,
        lower,
        lowerLeft,
        left
      )

  def apply(width: Int, height: Int): CellPlane =
    new CellPlane(width, height, Vector.fill(width * height)(Dead))

final class CellPlaneBuffer(
    val width: Int,
    val height: Int,
    val filler: Cell
) extends CellPlaneBase:
  private val cells = ArrayBuffer.fill[Cell](width * height)(filler)

  def update(cellCoords: CellCoordinates, cell: Cell): Unit =
    cells(cellCoords.toPlaneCoordinates) = cell

  def build(): CellPlane =
    new CellPlane(width, height, cells.toVector)

object CellPlaneBuffer:
  def apply(width: Int, height: Int, filler: Cell): CellPlaneBuffer =
    new CellPlaneBuffer(width, height, filler)
