package io.iyyel.game.of.life.logic

import io.iyyel.game.of.life.logic.CellPlane.{Cell, CellCoordinates}

trait CellPlaneContainer private[logic]:
  val cellPlane: CellPlane

  def getCell(cellCoords: CellCoordinates): Cell =
    cellPlane.getCell(cellCoords)
