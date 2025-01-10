import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.input.KeyCode
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Pane

// TODO: split into class files and folders and subfolders
// TODO: add tests once prototype is finished before doing new features

case class Board(width: Double = 1000, height: Double = 1000) {
  private val image = new Image(
    getClass.getResourceAsStream("/view/background.png"),
    width, // width
    height, // height
    true, // preserve ratio
    true // smooth
  )
  val view = new ImageView(image) {
    fitWidth = width
    fitHeight = height
    preserveRatio = true
  }

}

sealed trait Entity {
  val x: Double
  val y: Double
  val width: Double
  val height: Double
  val moveSpeed: Double

  val rightHitbox: Double = x + width
  val leftHitbox: Double = x
  val bottomHitbox: Double = y + height
  val topHitbox: Double = y
}

sealed trait Projectile extends Entity
case class Bullet(
    x: Double,
    y: Double,
    width: Double = 5,
    height: Double = 15,
    moveSpeed: Double = 20
) extends Projectile
//{
//  private val image = new Image(getClass.getResourceAsStream("/view/bullet.jpg"))
//  val view = new ImageView(image) {
//    fitWidth = width
//    fitHeight = height
//    preserveRatio = true
//  }
//}

case class Player(
    x: Double = 200,
    y: Double = 200,
    width: Double = 50,
    height: Double = 100,
    moveSpeed: Double = 25,
    bullets: List[Bullet] = Nil
) extends Entity {
  private val image = new Image(
    getClass.getResourceAsStream("/view/player.png"),
    width, // width
    height, // height
    false, // preserve ratio
    true // smooth
  )
  val playerX: Double = x
  val playerY: Double = y
  println(s"($playerX, $playerY)")
  val view: ImageView =
    new ImageView(image) {
      fitWidth = width
      fitHeight = height
      preserveRatio = true
      layoutX = playerX
      layoutY = playerY
    }
}

sealed trait Enemy extends Entity {
  val bullets: List[Bullet]
//  private val image = new Image(getClass.getResourceAsStream("/view/enemy.jpg"))
//  val view = new ImageView(image) {
//    fitWidth = width
//    fitHeight = height
//    preserveRatio = true
//  }
}

// TODO: add imageView for Minions
case class Minion(
    x: Double,
    y: Double,
    width: Double = 20,
    height: Double = 20,
    moveSpeed: Double = 10,
    bullets: List[Bullet] = Nil
) extends Enemy

// TODO: add imageView for Bosses
case class Boss(
    x: Double,
    y: Double,
    width: Double = 60,
    height: Double = 60,
    moveSpeed: Double = 10,
    bullets: List[Bullet] = Nil
) extends Enemy
case class Enemies(fleet: List[Enemy])

case class State(board: Board, player: Player, enemies: Enemies) {

  // TODO: create a Collision class and move this logic there
  def playerEnemiesCollision(
      player: Player,
      enemies: Enemies
  ): Boolean = {
    enemies.fleet.exists(enemy =>
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
      enemies: Enemies
  ): Boolean = {
    enemies.fleet.exists { enemy =>
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
      enemiesMovementUpdated: Enemies
  ): Boolean = {
    enemiesMovementUpdated.fleet.exists { enemy =>
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
  def updateState(distance: Int): State = {
    // Calculate player action
    val (
      playerMovementX: Double,
      playerMovementY: Double,
      playerBullet: (Double, Double)
    ) = distance match {
      case 1 => (player.x, player.y - player.moveSpeed, (0, 0))
      case 2 => (player.x, player.y + player.moveSpeed, (0, 0))
      case 3 => (player.x - player.moveSpeed, player.y, (0, 0))
      case 4 => (player.x + player.moveSpeed, player.y, (0, 0))
      case 5 => (player.x, player.y, (player.x + player.width / 2, player.y))
      case _ => (player.x, player.y, (0, 0))
    }

    // Win condition
    if (enemies.fleet.isEmpty) {
      println("Victory")
      System.exit(0)
    }

    // Calculate player's bullets movement
    val playerBulletsMovementUpdate = {
      val updatedBullets: List[Bullet] = player.bullets.collect {
        case bullet if bullet.y - bullet.moveSpeed >= 0 =>
          Bullet(x = bullet.x, y = bullet.y - bullet.moveSpeed)
      }
      if (playerBullet != (0, 0))
        Bullet(playerBullet._1, playerBullet._2) :: updatedBullets
      else
        updatedBullets
    }
    // TODO: Calculate enemies' bullets movement

    // Calculate player movement
    val playerMovementUpdated: Player = Player(
      x = playerMovementX,
      y = playerMovementY,
      bullets = playerBulletsMovementUpdate
    )

    // TODO: Calculate enemies movement
    val enemiesMovementUpdated = enemies

    // Calculate enemies collision with player bullets
    val updatedEnemies: Enemies = Enemies(
      enemiesMovementUpdated.fleet.collect {
        case minion
            if !enemyPlayerBulletsCollision(
              playerBulletsMovementUpdate,
              minion
            ) =>
          minion
        // TODO: bosses have more hitpoints
        case boss
            if !enemyPlayerBulletsCollision(
              playerBulletsMovementUpdate,
              boss
            ) =>
          boss
      }
    )
    // Calculate player bullets' collision with enemies
    val updatedPlayerBullets: List[Bullet] =
      playerBulletsMovementUpdate.collect {
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

  def renderRectangle(
      xRect: Double,
      yRect: Double,
      widthRect: Double,
      heightRet: Double,
      color: Color
  ): Rectangle =
    new Rectangle {
      x = xRect
      y = yRect
      width = widthRect
      height = heightRet
      fill = color
    }

  def renderEntities: List[Rectangle] = {
    val playerRect = renderRectangle(
      player.x,
      player.y,
      player.width,
      player.height,
      Color.Blue
    )
    val playerBulletsRects = player.bullets.map { bullet =>
      renderRectangle(
        bullet.x,
        bullet.y,
        bullet.width,
        bullet.height,
        Color.Blue
      )
    }
    val playerRects = playerBulletsRects // playerRect :: playerBulletsRects

    val enemiesRects = enemies.fleet.flatMap {
      case minion: Minion =>
        val minionRect = renderRectangle(
          minion.x,
          minion.y,
          minion.width,
          minion.height,
          Color.Red
        )
        val minionBullets = minion.bullets.map { bullet =>
          renderRectangle(
            bullet.x,
            bullet.y,
            bullet.width,
            bullet.height,
            Color.DarkRed
          )
        }
        minionRect :: minionBullets
      case boss: Boss =>
        val bossRect =
          renderRectangle(boss.x, boss.y, boss.width, boss.height, Color.Orange)
        val bossBullets = boss.bullets.map { bullet =>
          renderRectangle(
            bullet.x,
            bullet.y,
            bullet.width,
            bullet.height,
            Color.DarkOrange
          )
        }
        bossRect :: bossBullets
    }

    playerRects ::: enemiesRects
  }
}

// TODO; make this pure with cats IO
object World extends JFXApp3 {
  private def worldLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25) // Run program at 25 fps
    }.flatMap(_ => Future(worldLoop(update)))

  override def start(): Unit = {
    val board: Board = Board()
    val player: Player = Player()
    val minion: Minion = Minion(x = 100, y = 100)
    val boss: Boss = Boss(x = 300, y = 100)
    val enemies: Enemies = Enemies(fleet = List(boss, minion))

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
          children = Seq(board.view) ++ Seq(state.value.player.view) ++ state.value.renderEntities
        }
        onKeyPressed = key =>
          key.code match {
            case KeyCode.W     => distance.value = 1
            case KeyCode.S     => distance.value = 2
            case KeyCode.A     => distance.value = 3
            case KeyCode.D     => distance.value = 4
            case KeyCode.Space => distance.value = 5
            case _             => ???
          }

        frame.onChange {
          Platform.runLater {
            content = new Pane {
              children =
                Seq(board.view) ++ Seq(state.value.player.view) ++ state.value.renderEntities
            }
          }
        }

      }
    }

    worldLoop(() => frame.update(frame.value + 1))
  }

}
