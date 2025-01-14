package spaceinvaders42

import scalafx.scene.Node

// stage == 1 => initial stage, minions
// stage == 2 => final stage, boss

case class State(
    board: Board,
    player: Player,
    enemies: List[Enemy],
    stage: Int
) {
  def render: List[Node] = {
    val boardView = board.render()
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
    boardView :: playerViews ::: enemiesViews
  }

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

  def generateEnemies(board: Board): List[Enemy] = {
    val tmpMinion: Minion = Minion(0, 0)
    (for {
      x <-
        tmpMinion.width until (board.width / 5 - tmpMinion.width) by (tmpMinion.width * 2)
      y <-
        tmpMinion.height until (board.height / 5 - tmpMinion.height) by (tmpMinion.height * 2)
    } yield Minion(x, y)).toList // .reverse
  }

  def generateBoss(board: Board): List[Enemy] = {
    val tmpBoss: Boss = Boss(0, 0)
    val boss: Boss = Boss(board.width / 2 - tmpBoss.width, tmpBoss.height)
    List(boss)
  }

  def update(playerActionCode: Int): State = {
    // Stage update
    // Stage 0: game starts
    val (updatedStage, updatedEnemiesStage) = stage match {
      // Stage 1: generate minions
      case 0 if enemies.isEmpty =>
        val newEnemies = generateEnemies(board)
        (1, newEnemies)
      case 1 if enemies.isEmpty =>
        // Stage 2: generate boss
        val newEnemies = generateBoss(board)
        (2, newEnemies)
      case 2 if enemies.isEmpty =>
        // Final stage: win
        println("Victory")
        System.exit(0)
        (2, enemies)
      case _ =>
        (stage, enemies)
    }
    // Refresh state after transitioning a stage
    if (updatedStage != stage) {
      return State(board, player, updatedEnemiesStage, updatedStage)
    }

    // Calculate player action
    val playerAfterAction: Player = player.action(playerActionCode)

    // Calculate player's bullets movement
    val playerBulletsMovementUpdated: List[Bullet] =
      playerAfterAction.updateBullets(board)

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
        playerCollidesWithEnemy(
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
        enemiesBulletsCollideWithPlayer(
          playerMovementUpdated,
          enemiesMovementUpdated
        )
      ) {
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
    State(board, updatedPlayer, updatedEnemies, updatedStage)
  }
}
