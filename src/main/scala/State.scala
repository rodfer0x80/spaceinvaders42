package spaceinvaders42

import scalafx.scene.Node

case class State(board: Board, player: Player, enemies: List[Enemy]) {

  def playerCollidesWithEnemy(
      player: Player,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists(enemy => player.collidesWith(enemy))
  }

  def playerCollidesWithBorder(
      player: Player,
      board: Board
  ): Boolean = {
    player.x <= 0 ||
    player.x >= board.width - player.width ||
    player.y <= 0 ||
    player.y >= board.height - player.height
  }

  def playerBulletCollidesWithEnemy(
      playerBullet: Bullet,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists { enemy =>
      playerBullet.collidesWith(enemy)
    }
  }

  def playerBulletsCollideWithEnemy(
      playerBullets: List[Bullet],
      enemy: Enemy
  ): Boolean = {
    playerBullets.exists { playerBullet =>
      playerBullet.collidesWith(enemy)
    }
  }

  def enemiesBulletsCollideWithPlayer(
      player: Player,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists { enemy =>
      enemy.bullets.exists { enemyBullet =>
        enemyBullet.collidesWith(player)
      }
    }
  }

  def checkWin(enemies: List[Enemy]): Unit = {
    if (enemies.isEmpty) {
      println("Victory")
      System.exit(0)
    }
  }

  // TODO: make state a class in a file and move logic there, only call for update state here
  // TODO: move movement and shooting logic to case classes
  def updateState(playerActionCode: Int): State = {
    // Win condition
    checkWin(enemies)

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
            if !playerBulletsCollideWithEnemy(
              playerBulletsMovementUpdated,
              minion
            ) =>
          minion
        // TODO: bosses have more hitpoints
        case boss
            if !playerBulletsCollideWithEnemy(
              playerBulletsMovementUpdated,
              boss
            ) =>
          boss
      }

    // Calculate player bullets' collision with enemies
    val updatedPlayerBullets: List[Bullet] =
      playerBulletsMovementUpdated.collect {
        case playerBullet
            if !playerBulletCollidesWithEnemy(
              playerBullet,
              enemiesMovementUpdated
            ) =>
          playerBullet
      }

    // Calculate player update
    val updatedPlayer: Player = {
      // If player hits an enemy the player loses
      if (
        playerCollidesWithEnemy(playerMovementUpdated, enemiesMovementUpdated)
      ) {
        // Player()
        println("Defeat")
        System.exit(0)
        Player()
      }
      // If player is hit by an enemy bullet player loses
      else if (
        playerCollidesWithEnemy(
          playerMovementUpdated,
          enemiesMovementUpdated
        )
      ) {
        // Player()
        println("Defeat")
        System.exit(0)
        Player()
      }
      // If player hits a border don't allow further movement towards it
      else if (playerCollidesWithBorder(playerMovementUpdated, board))
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

  def renderEntities: List[Node] = {
    val playerRect = player.render()
    val playerBulletsRects = player.bullets.map { bullet =>
      bullet.render()
    }
    val playerRects = playerRect :: playerBulletsRects

    val enemiesRects = enemies.flatMap { enemy =>
      val enemyRect = enemy.render()
      val enemyBullets = enemy.bullets.map { bullet =>
        bullet.render()
      }
      enemyRect :: enemyBullets
    }

    playerRects ::: enemiesRects
  }
}
