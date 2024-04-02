package io.iyyel.game.of.life.controls

import io.iyyel.game.of.life.logic.CellPlane.{Alive, CellCoordinates, Dead}
import io.iyyel.game.of.life.logic.{State, Universe, UniverseChanges}
import io.iyyel.game.of.life.util.Extensions.*
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.{Canvas, Div, Span}

import scala.scalajs.js

final class UniverseView(
    rootElement: Div,
    runningState: State[Boolean],
    universeState: State[Universe],
    universeChangesState: State[UniverseChanges],
    zoomState: State[Double]
):
  private val GridColor = "#EBEBEB"
  private val AliveCellColor = "#8ADB87"
  private val DeadCellColor = "#FAFCF8"
  private val CursorColor = "#C6EDC4"
  private val CursorDownColor = "#79D676"

  private val GridOffset = 0
  private val CellBorderWidth = 1
  private val CellSize = 16

  private var width = 0
  private var height = 0
  private var leftBorder = 0
  private var rightBorder = 0
  private var topBorder = 0
  private var bottomBorder = 0

  val cellPlaneClickState: State[CellCoordinates] =
    State(CellCoordinates(-1, -1))
  private val lastDrewCursorState: State[Option[CellCoordinates]] =
    State(None)

  private val canvas = rootElement.getChild[Canvas]("universe-view")
  private val ctx: CanvasRenderingContext2D =
    canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
  ctx.imageSmoothingEnabled = true

  private val spanCellCoords =
    rootElement.getChild[Span]("span-cell-coords")
  private val spanCellCoordRow =
    spanCellCoords.getChild[Span]("span-cell-coord-row")
  private val spanCellCoordCol =
    spanCellCoords.getChild[Span]("span-cell-coord-col")

  canvas.onclick = (e: dom.MouseEvent) => canvasOnClick(e)
  canvas.onmousemove = (e: dom.MouseEvent) => canvasOnMouseMove(e)
  canvas.onmouseleave = (e: dom.MouseEvent) => canvasOnMouseLeave(e)
  canvas.onmousedown = (e: dom.MouseEvent) => canvasOnMouseDown(e)
  canvas.onmouseup = (e: dom.MouseEvent) => canvasOnMouseUp(e)

  zoomState.observe(_ => init())
  universeState.observe(drawUniverse)
  universeChangesState.observeAfter(drawUniverseChanges)
  lastDrewCursorState.observe:
    case Some(CellCoordinates(row, col)) =>
      spanCellCoords.removeClass("hidden")
      spanCellCoordRow.textContent = row.toString
      spanCellCoordCol.textContent = col.toString
    case _ =>
      spanCellCoords.addClass("hidden")
      spanCellCoordRow.textContent = "-1"
      spanCellCoordCol.textContent = "-1"

  private def init(): Unit =
    width = universeState.now().width
    height = universeState.now().height
    val zoom = zoomState.now()

    leftBorder = GridOffset
    rightBorder = (GridOffset + width * CellSize * zoom).toInt
    topBorder = GridOffset
    bottomBorder = (GridOffset + height * CellSize * zoom).toInt

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    canvas.width = Math.ceil(rightBorder - leftBorder).toInt + 1
    canvas.height = Math.ceil(bottomBorder - topBorder).toInt + 1
    ctx.scale(zoom, zoom)

    ctx.translate(0.5, 0.5)
    drawGrid()

    // clear initial alive cells
    for
      row <- 0 until height
      col <- 0 until width
    yield
      val cellCoords = CellCoordinates(row, col)
      if universeState.now().getCell(cellCoords) == Alive then
        drawDeadCell(cellCoords)

    drawUniverse(universeState.now())

  private def drawGrid(): Unit =
    ctx.strokeStyle = GridColor
    ctx.lineWidth = CellBorderWidth

    val horizontalLineLength = width * CellSize
    val verticalLineLength = height * CellSize

    (0 to width)
      .map(GridOffset + _ * CellSize)
      .foreach: offset =>
        ctx.moveTo(offset, GridOffset)
        ctx.lineTo(offset, GridOffset + horizontalLineLength)

    (0 to height)
      .map(GridOffset + _ * CellSize)
      .foreach: offset =>
        ctx.moveTo(GridOffset, offset)
        ctx.lineTo(GridOffset + verticalLineLength, offset)

    ctx.stroke()

  private def drawUniverse(universe: Universe): Unit =
    if universe.width != width || universe.height != height then init()
    val changesFromBigBang = UniverseChanges(universe)
    drawUniverseChanges(changesFromBigBang)

  private def drawUniverseChanges(changes: UniverseChanges): Unit =
    clearLastDrewCursor()
    lastDrewCursorState.set(None)
    for
      row <- 0 until height
      col <- 0 until width
    yield changes.getCell(CellCoordinates(row, col)) match
      case Alive => drawAliveCell(CellCoordinates(row, col))
      case Dead  => drawDeadCell(CellCoordinates(row, col))
      case _     =>

  private def drawAliveCell(cellCoords: CellCoordinates): Unit =
    val (cellCenterX, cellCenterY) = cellCoords.cellCenter
    val centerOffset = (CellSize * 0.1) / 2
    val cellSizeOffset = 0.9
    ctx.fillStyle = AliveCellColor
    ctx.beginPath()
    ctx.rect(
      cellCenterY + centerOffset,
      cellCenterX + centerOffset,
      CellSize * cellSizeOffset,
      CellSize * cellSizeOffset
    )
    ctx.fill()

  private def drawDeadCell(cellCoords: CellCoordinates): Unit =
    val (cornerX, cornerY) = cellCoords.topLeftCorner
    ctx.fillStyle = DeadCellColor
    ctx.beginPath()
    ctx.fillRect(cornerY, cornerX, CellSize, CellSize)
    ctx.fill()
    ctx.strokeRect(cornerY, cornerX, CellSize, CellSize)

  private def drawCursor(
      cellCoords: CellCoordinates,
      mouseDown: Boolean
  ): Unit =
    val (cellCenterX, cellCenterY) = cellCoords.cellCenter
    val centerOffset = (CellSize * 0.1) / 2
    val cellSizeOffset = 0.9
    val cursorSize =
      if mouseDown then CellSize * cellSizeOffset
      else CellSize * cellSizeOffset

    ctx.fillStyle =
      if mouseDown then CursorDownColor
      else CursorColor
    ctx.beginPath()
    ctx.rect(
      cellCenterY + centerOffset,
      cellCenterX + centerOffset,
      cursorSize,
      cursorSize
    )
    ctx.fill()

  private def clearLastDrewCursor(): Unit =
    lastDrewCursorState.now() match
      case Some(cellCoords) =>
        drawDeadCell(cellCoords)
        universeState.now().getCell(cellCoords) match
          case Dead  => drawDeadCell(cellCoords)
          case Alive => drawAliveCell(cellCoords)
          case _     =>
      case _ =>

  private def canvasOnClick(e: dom.MouseEvent): Unit =
    clientToCellCoordinates(e.clientX, e.clientY) match
      case Some(cellCoords) if !runningState.now() =>
        cellPlaneClickState.set(cellCoords)
      case _ =>

  private def canvasOnMouseMove(e: dom.MouseEvent): Unit =
    clientToCellCoordinates(e.clientX, e.clientY) match
      case Some(cellCoords) if !runningState.now() =>
        drawCursor(cellCoords, false)
        lastDrewCursorState.now() match
          case Some(lastCursorCellCoords)
              if lastCursorCellCoords != cellCoords =>
            clearLastDrewCursor()
          case _ =>
        lastDrewCursorState.set(Some(cellCoords))
      case _ =>

  private def canvasOnMouseLeave(e: dom.MouseEvent): Unit =
    if !runningState.now() then clearLastDrewCursor()
    lastDrewCursorState.set(None)

  private def canvasOnMouseDown(e: dom.MouseEvent): Unit =
    lastDrewCursorState.now() match
      case Some(cellCoords) if !runningState.now() =>
        drawCursor(cellCoords, true)
      case _ =>

  private def canvasOnMouseUp(e: dom.MouseEvent): Unit =
    lastDrewCursorState.now() match
      case Some(cellCoords) if !runningState.now() =>
        clearLastDrewCursor()
        drawCursor(cellCoords, false)
      case _ =>

  private def clientToCellCoordinates(
      clientX: Double,
      clientY: Double
  ): Option[CellCoordinates] =
    val clientRect = canvas.getBoundingClientRect()
    val x = clientX - clientRect.left
    val y = clientY - clientRect.top
    if x >= leftBorder && x < rightBorder && y >= topBorder && y < bottomBorder
    then
      val zoom = zoomState.now()
      val row = Math.floor((y - topBorder) / zoom / CellSize).toInt
      val col = Math.floor((x - leftBorder) / zoom / CellSize).toInt
      Some(CellCoordinates(row, col))
    else None

  extension (cellCoords: CellCoordinates)
    private def topLeftCorner: (Double, Double) =
      (
        GridOffset + cellCoords.row * CellSize,
        GridOffset + cellCoords.col * CellSize
      )

    private def cellCenter: (Double, Double) =
      (
        GridOffset + cellCoords.row * CellSize,
        GridOffset + cellCoords.col * CellSize
      )
