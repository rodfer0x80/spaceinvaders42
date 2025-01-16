package spaceinvaders42

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.Pane

object Main extends JFXApp3 {
  def render(state: State): List[Node] = {
    val boardView = state.board.render()
    val playerView = state.player.render()
    val playerBulletsView = state.player.bullets.map { bullet =>
      bullet.render()
    }
    val playerViews = playerView :: playerBulletsView
    val enemiesViews = state.enemies.flatMap { enemy =>
      val enemyView = enemy.render()
      val enemyBulletsView = enemy.bullets.map { bullet =>
        bullet.render()
      }
      enemyView :: enemyBulletsView
    }
    boardView :: playerViews ::: enemiesViews
  }
  def worldLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25 * 2) // Run program at 25 fps
    }.flatMap(_ => Future(worldLoop(update)))

  override def start(): Unit = {
    val board: Board = Board() // TODO: make this an object and stop passing it around
    val player: Player = Player()
    val enemies: List[Enemy] = List()
    val state = ObjectProperty(State(board, player, enemies, 0))
    val frame = IntegerProperty(0)
    frame.onChange {
      val input = KeyboardInput.getKey
      state.update(state.value.update(input))
    }
    stage = new JFXApp3.PrimaryStage {
      width = board.width
      height = board.height
      onCloseRequest = _ => {
        AudioControl.shutdown()
      }
      scene = new Scene {
        onKeyPressed = (ke: KeyEvent) => {
          KeyboardInput.keyPressed(ke.code)
//          if (ke.code == KeyCode.Space) {
//            GameAudio.playShootSound()
//          }
        }
        onKeyReleased = (ke: KeyEvent) => {
          KeyboardInput.keyReleased(ke.code)
        }
        content = new Pane {
          children = render(state.value)
          AudioControl.startBackgroundMusic()
        }
        frame.onChange {
          Platform.runLater {
            content = new Pane {
              children = render(state.value)
            }
          }
        }
      }
    }
    worldLoop(() => frame.update(frame.value + 1))
  }

}