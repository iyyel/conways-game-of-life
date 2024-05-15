package io.iyyel.game.of.life.controls;
import org.scalajs.dom
import org.scalajs.dom.html.*
import io.iyyel.game.of.life.logic.Universe
import io.iyyel.game.of.life.logic.State
import io.iyyel.game.of.life.util.Extensions.getChild
import io.iyyel.game.of.life.logic.Universes

final class PredefinedUniverseModal(
    rootElement: Div,
    universeState: State[Universe]
):
  val btnGlider =
    rootElement.getChild[Button]("btn-glider")

  val btnBeehive =
    rootElement.getChild[Button]("btn-beehive")

  val btnBlinker =
    rootElement.getChild[Button]("btn-blinker")

  val btnToad =
    rootElement.getChild[Button]("btn-toad")

  val btnBeacon =
    rootElement.getChild[Button]("btn-beacon")

  val btnTwoGliders =
    rootElement.getChild[Button]("btn-two-gliders")

  val btnLightweightSpaceship =
    rootElement.getChild[Button]("btn-lightweight-spaceship")

  val btnPulsar =
    rootElement.getChild[Button]("btn-pulsar")

  val btnGliderGun =
    rootElement.getChild[Button]("btn-gospers-glider-gun")

  btnGlider.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.GLIDER)

  btnBeehive.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.BEEHIVE)

  btnBlinker.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.BLINKER)

  btnToad.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.TOAD)

  btnBeacon.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.BEACON)

  btnTwoGliders.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.TWO_GLIDERS)

  btnLightweightSpaceship.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.LIGHTWEIGHT_SPACESHIP)

  btnPulsar.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.PULSAR)

  btnGliderGun.onclick = _ =>
    dom.document.location.href = "#"
    universeState.set(Universes.GLIDER_GUN)
