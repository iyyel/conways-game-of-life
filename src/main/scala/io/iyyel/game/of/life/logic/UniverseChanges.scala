package io.iyyel.game.of.life.logic

sealed case class UniverseChanges(
    cellPlane: CellPlane
) extends CellPlaneContainer

object UniverseChanges:
  def apply(universe: Universe): UniverseChanges =
    UniverseChanges(universe.cellPlane)
