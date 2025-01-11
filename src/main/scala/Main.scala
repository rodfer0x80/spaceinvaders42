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
