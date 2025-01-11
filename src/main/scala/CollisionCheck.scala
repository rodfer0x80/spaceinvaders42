package spaceinvaders42

class CollisionCheck {
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
}
