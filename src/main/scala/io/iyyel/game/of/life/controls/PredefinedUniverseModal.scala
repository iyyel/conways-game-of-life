package io.iyyel.game.of.life.controls;

import io.iyyel.game.of.life.util.Extensions.getChild
import io.iyyel.game.of.life.logic.UniverseWithEpoch
import io.iyyel.game.of.life.logic.Universes
import io.iyyel.game.of.life.logic.Universe
import io.iyyel.game.of.life.logic.State

import org.scalajs.dom
import org.scalajs.dom.html.*

final class PredefinedUniverseModal(
    rootElement: Div,
    runningState: State[Boolean],
    universeState: State[Universe],
    currentEpochState: State[Int],
    universeEpochsState: State[List[UniverseWithEpoch]]
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

  btnGlider.onclick = _ => predefinedUniverseClick(Universes.GLIDER)

  btnBeehive.onclick = _ => predefinedUniverseClick(Universes.BEEHIVE)

  btnBlinker.onclick = _ => predefinedUniverseClick(Universes.BLINKER)

  btnToad.onclick = _ => predefinedUniverseClick(Universes.TOAD)

  btnBeacon.onclick = _ => predefinedUniverseClick(Universes.BEACON)

  btnTwoGliders.onclick = _ => predefinedUniverseClick(Universes.TWO_GLIDERS)

  btnLightweightSpaceship.onclick = _ =>
    predefinedUniverseClick(Universes.LIGHTWEIGHT_SPACESHIP)

  btnPulsar.onclick = _ => predefinedUniverseClick(Universes.PULSAR)

  btnGliderGun.onclick = _ => predefinedUniverseClick(Universes.GLIDER_GUN)

  def predefinedUniverseClick(universe: Universe): Unit =
    dom.document.location.href = "#"
    runningState.set(false)
    universeState.set(universe)
    val newEpoch = 1
    currentEpochState.set(newEpoch)
    universeEpochsState.set(
      List(UniverseWithEpoch(universeState.now(), newEpoch))
    )
