package jp.assasans.protanki.server.battles.weapons

import jp.assasans.protanki.server.battles.*
import jp.assasans.protanki.server.client.send
import jp.assasans.protanki.server.client.weapons.twins.Fire
import jp.assasans.protanki.server.client.weapons.twins.FireStatic
import jp.assasans.protanki.server.client.weapons.twins.FireTarget
import jp.assasans.protanki.server.client.toJson
import jp.assasans.protanki.server.commands.Command
import jp.assasans.protanki.server.commands.CommandName
import jp.assasans.protanki.server.garage.ServerGarageUserItemWeapon

class TwinsWeaponHandler(
  player: BattlePlayer,
  weapon: ServerGarageUserItemWeapon
) : WeaponHandler(player, weapon) {
  suspend fun fire(fire: Fire) {
    val tank = player.tank ?: throw Exception("No Tank")
    val battle = player.battle

    Command(CommandName.Shot, tank.id, fire.toJson()).send(battle.players.exclude(player).ready())
  }

  suspend fun fireStatic(static: FireStatic) {
    val tank = player.tank ?: throw Exception("No Tank")
    val battle = player.battle

    Command(CommandName.ShotStatic, tank.id, static.toJson()).send(battle.players.exclude(player).ready())
  }

  suspend fun fireTarget(target: FireTarget) {
    val sourceTank = player.tank ?: throw Exception("No Tank")
    val battle = player.battle

    val targetTank = battle.players
      .mapNotNull { player -> player.tank }
      .single { tank -> tank.id == target.target }
    if(targetTank.state != TankState.Active) return

    battle.damageProcessor.dealDamage(sourceTank, targetTank, 25.0, false)

    Command(CommandName.FireTarget, sourceTank.id, target.toJson()).send(battle.players.exclude(player).ready())
  }
}
