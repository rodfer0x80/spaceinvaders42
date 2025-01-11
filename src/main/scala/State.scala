package spaceinvaders42

import scalafx.scene.Node

case class State(board: Board, player: Player, enemies: List[Enemy]) {
  def update(playerActionCode: Int): State = {
    // Win condition
    if (enemies.isEmpty) {
      println("Victory")
      System.exit(0)
    }

    val collisionCheck: CollisionCheck = CollisionCheck()

    // Calculate player action
    val playerAfterAction: Player = player.action(playerActionCode)

    // Calculate player's bullets movement
    val playerBulletsMovementUpdated: List[Bullet] =
      playerAfterAction.updateBullets()

    // TODO: Calculate enemies' bullets movement
    val enemiesBulletsMovementUpdated: List[Bullet] = Nil

    // Calculate player movement
    val playerMovementUpdated: Player = Player(
      x = playerAfterAction.x,
      y = playerAfterAction.y,
      bullets = playerBulletsMovementUpdated
    )

    // TODO: Calculate enemies movement
    val enemiesMovementUpdated = enemies

    // Calculate enemies collision with player bullets
    val updatedEnemies: List[Enemy] =
      enemiesMovementUpdated.collect {
        case minion
            if !collisionCheck.playerBulletsCollideWithEnemy(
              playerBulletsMovementUpdated,
              minion
            ) =>
          minion
        // TODO: bosses have more hitpoints
        case boss
            if !collisionCheck.playerBulletsCollideWithEnemy(
              playerBulletsMovementUpdated,
              boss
            ) =>
          boss
      }

    // Calculate player bullets' collision with enemies
    val updatedPlayerBullets: List[Bullet] =
      playerBulletsMovementUpdated.collect {
        case playerBullet
            if !collisionCheck.playerBulletCollidesWithEnemy(
              playerBullet,
              enemiesMovementUpdated
            ) =>
          playerBullet
      }

    // Calculate player update
    val updatedPlayer: Player = {
      // If player hits an enemy the player loses
      if (
        collisionCheck.playerCollidesWithEnemy(
          playerMovementUpdated,
          enemiesMovementUpdated
        )
      ) {
        println("Defeat")
        System.exit(0)
        Player()
      }
      // If player is hit by an enemy bullet player loses
      else if (
        collisionCheck.enemiesBulletsCollideWithPlayer(
          playerMovementUpdated,
          enemiesMovementUpdated
        )
      ) {
        println("Defeat")
        System.exit(0)
        Player()
      }
      // If player hits a border don't allow further movement towards it
      else if (
        collisionCheck.playerCollidesWithBorder(playerMovementUpdated, board)
      )
        Player(x = player.x, y = player.y, bullets = updatedPlayerBullets)
      else
        Player(
          x = playerMovementUpdated.x,
          y = playerMovementUpdated.y,
          bullets = updatedPlayerBullets
        )
    }
    State(board, updatedPlayer, updatedEnemies)
  }

  def render: List[Node] = {
    val playerView = player.render()
    val playerBulletsView = player.bullets.map { bullet =>
      bullet.render()
    }
    val playerViews = playerView :: playerBulletsView
    val enemiesViews = enemies.flatMap { enemy =>
      val enemyView = enemy.render()
      val enemyBulletsView = enemy.bullets.map { bullet =>
        bullet.render()
      }
      enemyView :: enemyBulletsView
    }
    playerViews ::: enemiesViews
  }
}
