package spaceinvaders42

import scalafx.scene.Node

// --
// NOTE:
// stage == 1 => initial stage, minions
// stage == 2 => final stage, boss
// --
case class State(
    player: Player,
    enemies: List[Enemy],
    stage: Int
) {
  def playerCollidesWithEnemy(
      player: Player,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists(enemy => player `collidesWith` enemy)
  }

  def playerBulletCollidesWithEnemy(
      playerBullet: Bullet,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists { enemy =>
      playerBullet `collidesWith` enemy
    }
  }

  def playerBulletsCollideWithEnemy(
      playerBullets: List[Bullet],
      enemy: Enemy
  ): Boolean = {
    playerBullets.exists { playerBullet =>
      playerBullet `collidesWith` enemy
    }
  }

  def enemiesBulletsCollideWithPlayer(
      player: Player,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists { enemy =>
      enemy.bullets.exists { enemyBullet =>
        enemyBullet `collidesWith` player
      }
    }
  }

  def generateEnemies(): List[Enemy] = {
    val tmpMinion: Minion = Minion(0, 0)
    (for {
      x <-
        tmpMinion.width until (Board.width / 5 - tmpMinion.width) by (tmpMinion.width * 2)
      y <-
        tmpMinion.height until (Board.height / 5 - tmpMinion.height) by (tmpMinion.height * 2)
    } yield Minion(x, y)).toList // .reverse
  }

  def generateBoss(): List[Enemy] = {
    val tmpBoss: Boss = Boss(0, 0)
    val boss: Boss = Boss(Board.width / 2 - tmpBoss.width, tmpBoss.height)
    List(boss)
  }

  var paused: Boolean = false

  def update(input: Int): State = {
    // Pause on "Enter"
    if (input == 11) 
      paused = !paused 
    if (paused) return this
    
    // Stage update
    // Stage 0: game starts
    val (updatedStage, updatedEnemiesStage) = stage match {
      // Stage 1: generate minions
      case 0 if enemies.isEmpty =>
        val newEnemies = generateEnemies()
        (1, newEnemies)
      case 1 if enemies.isEmpty =>
        // Stage 2: generate boss
        val newEnemies = generateBoss()
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
      return State(player, updatedEnemiesStage, updatedStage)
    }

    // Calculate player action
    val playerAfterAction: Player = player.action(input)

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
    // val enemiesMovementUpdated = enemies
    val enemiesMovementUpdated: List[Enemy] = enemies.map(_.action())

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

    // --
    // TODO: clean this shit up,
    // player logic for border collision = nonaction is already in the class
    // --
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
      else if (playerMovementUpdated.collidesWithBorder())
        Player(x = player.x, y = player.y, bullets = updatedPlayerBullets)
      else
        Player(
          x = playerMovementUpdated.x,
          y = playerMovementUpdated.y,
          bullets = updatedPlayerBullets
        )
    }
    State(updatedPlayer, updatedEnemies, updatedStage)
  }
}
