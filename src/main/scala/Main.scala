package spaceinvaders42

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.input.KeyCode
import scalafx.scene.layout.Pane
import scalafx.scene.Node

case class State(board: Board, player: Player, enemies: List[Enemy]) {

  // TODO: move collision logic to case classes
  def playerEnemiesCollision(
      player: Player,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists(enemy =>
      val horizontalOverlap =
        player.leftHitbox < enemy.rightHitbox && player.rightHitbox > enemy.leftHitbox
      val verticalOverlap =
        player.topHitbox < enemy.bottomHitbox && player.bottomHitbox > enemy.topHitbox
      horizontalOverlap && verticalOverlap
    )
  }

  def playerBorderCollision(
      player: Player,
      board: Board
  ): Boolean = {
    player.x <= 0 ||
    player.x >= board.width - player.width ||
    player.y <= 0 ||
    player.y >= board.height - player.height
  }

  def playerBulletEnemiesCollision(
      playerBullet: Bullet,
      enemies: List[Enemy]
  ): Boolean = {
    enemies.exists { enemy =>
      val horizontalOverlap =
        playerBullet.leftHitbox < enemy.rightHitbox && playerBullet.rightHitbox > enemy.leftHitbox
      val verticalOverlap =
        playerBullet.topHitbox < enemy.bottomHitbox && playerBullet.bottomHitbox > enemy.topHitbox
      horizontalOverlap && verticalOverlap
    }
  }

  def enemyPlayerBulletsCollision(
      playerBullets: List[Bullet],
      enemy: Enemy
  ): Boolean = {
    playerBullets.exists { bullet =>
      val horizontalOverlap =
        bullet.leftHitbox < enemy.rightHitbox && bullet.rightHitbox > enemy.leftHitbox
      val verticalOverlap =
        bullet.topHitbox < enemy.bottomHitbox && bullet.bottomHitbox > enemy.topHitbox
      horizontalOverlap && verticalOverlap
    }
  }

  def enemiesBulletPlayerCollision(
      playerMovementUpdated: Player,
      enemiesMovementUpdated: List[Enemy]
  ): Boolean = {
    enemiesMovementUpdated.exists { enemy =>
      enemy.bullets.exists { bullet =>
        val horizontalOverlap =
          bullet.leftHitbox < playerMovementUpdated.rightHitbox && bullet.rightHitbox > playerMovementUpdated.leftHitbox
        val verticalOverlap =
          bullet.topHitbox < playerMovementUpdated.bottomHitbox && bullet.bottomHitbox > playerMovementUpdated.topHitbox
        horizontalOverlap && verticalOverlap
      }
    }
  }

  // TODO: make state a class in a file and move logic there, only call for update state here
  // TODO: move movement and shooting logic to case classes
  def updateState(distance: Int): State = {
    // Calculate player action
    val (
      playerMovementX: Double,
      playerMovementY: Double,
      playerBullet: (Double, Double)
    ) = distance match {
      case 1 => (player.x, player.y - player.speed, (0.0, 0.0))
      case 2 => (player.x, player.y + player.speed, (0.0, 0.0))
      case 3 => (player.x - player.speed, player.y, (0.0, 0.0))
      case 4 => (player.x + player.speed, player.y, (0.0, 0.0))
      case 5 => (player.x, player.y, (player.x + player.width / 2, player.y))
      case _ => (player.x, player.y, (0.0, 0.0))
    }

    // Win condition
    if (enemies.isEmpty) {
      println("Victory")
      System.exit(0)
    }

    // Calculate player's bullets movement
    val playerBulletsMovementUpdated = {
      val updatedBullets: List[Bullet] = player.bullets.collect {
        case bullet if bullet.y - bullet.speed >= 0 =>
          Bullet(x = bullet.x, y = bullet.y - bullet.speed)
      }
      if (playerBullet != (0.0, 0.0))
        Bullet(playerBullet._1, playerBullet._2) :: updatedBullets
      else
        updatedBullets
    }
    // TODO: Calculate enemies' bullets movement

    // Calculate player movement
    val playerMovementUpdated: Player = Player(
      x = playerMovementX,
      y = playerMovementY,
      bullets = playerBulletsMovementUpdated
    )

    // TODO: Calculate enemies movement
    val enemiesMovementUpdated = enemies

    // Calculate enemies collision with player bullets
    val updatedEnemies: List[Enemy] =
      enemiesMovementUpdated.collect {
        case minion
            if !enemyPlayerBulletsCollision(
              playerBulletsMovementUpdated,
              minion
            ) =>
          minion
        // TODO: bosses have more hitpoints
        case boss
            if !enemyPlayerBulletsCollision(
              playerBulletsMovementUpdated,
              boss
            ) =>
          boss
      }

    // Calculate player bullets' collision with enemies
    val updatedPlayerBullets: List[Bullet] =
      playerBulletsMovementUpdated.collect {
        case playerBullet
            if !playerBulletEnemiesCollision(
              playerBullet,
              enemiesMovementUpdated
            ) =>
          playerBullet
      }

    // Calculate player update
    val updatedPlayer: Player = {
      // If player hits an enemy the player loses
      if (
        playerEnemiesCollision(playerMovementUpdated, enemiesMovementUpdated)
      ) {
        // Player()
        println("Defeat")
        System.exit(0)
        Player()
      }
      // If player is hit by an enemy bullet player loses
      else if (
        enemiesBulletPlayerCollision(
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
      else if (playerBorderCollision(playerMovementUpdated, board))
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

// TODO; make this pure with cats IO
object Main extends JFXApp3 {
  private def worldLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25 * 2) // Run program at 25 fps
    }.flatMap(_ => Future(worldLoop(update)))

  override def start(): Unit = {
    val board: Board = Board()
    val player: Player = Player()
    val minion: Minion = Minion(x = 100, y = 100)
    val boss: Boss = Boss(x = 300, y = 100)
    val enemies: List[Enemy] = List(boss, minion)

    val state = ObjectProperty(State(board, player, enemies))
    val frame = IntegerProperty(0)
    val distance = IntegerProperty(0) // Player starts standing still

    frame.onChange {
      state.update(state.value.updateState(distance.value))
      distance.value = 0 // Stop player action
    }

    stage = new JFXApp3.PrimaryStage {
      width = board.width
      height = board.height
      scene = new Scene {
        fill = Color.Grey
        content = new Pane {
          children = Seq(board.render()) ++ state.value.renderEntities
        }
        onKeyPressed = key =>
          key.code match {
            case KeyCode.W     => distance.value = 1 // up
            case KeyCode.S     => distance.value = 2 // down
            case KeyCode.A     => distance.value = 3 // left
            case KeyCode.D     => distance.value = 4 // right
            case KeyCode.Space => distance.value = 5 // fire
            case _             => distance.value = 0 // nop
          }

        frame.onChange {
          Platform.runLater {
            content = new Pane {
              children = Seq(board.render()) ++ state.value.renderEntities
            }
          }
        }

      }
    }

    worldLoop(() => frame.update(frame.value + 1))
  }

}
