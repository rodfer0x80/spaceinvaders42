package spaceinvaders42

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalafx.Includes.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.{IntegerProperty, ObjectProperty}
import scalafx.scene.{Node, Scene}
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.Pane

// --
// TODO:
// should Audio be called from each class when needed instead of Main?
// e.g. player shoots, boss spawns, board init, etc etc
// I kinda like the View extension instead of having a Render object or sth
// --

object Main extends JFXApp3 {
  def worldLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25 * 2) // Run program at 25 fps
    }.flatMap(_ => Future(worldLoop(update)))

  override def start(): Unit = {
    val player: Player = Player()
    val enemies: List[Enemy] = List()
    val state = ObjectProperty(State(player, enemies, 0))
    val frame = IntegerProperty(0)
    var paused = true
    frame.onChange {
      val input = Input.getKey
      // Quit on "Esc"
      if (input == 10)
        println("Quitting ...")
        System.exit(0)
      // Pause on "Enter"
      if (input == 11)
        paused = !paused
      state.update(state.value.update(input, paused))
    }
    stage = new JFXApp3.PrimaryStage {
      width = Board.width
      height = Board.height
      onCloseRequest = _ => {
        SoundFX.shutdown()
      }
      scene = new Scene {
        onKeyPressed = (ke: KeyEvent) => {
          Input.keyPressed(ke.code)
        }
        onKeyReleased = (ke: KeyEvent) => {
          Input.keyReleased(ke.code)
        }
        content = new Pane {
          children = {
            if (paused) {
              ViewFX.renderMenu()
            } else {
              ViewFX.render(state.value)
            }
          }
        }
        frame.onChange {
          Platform.runLater {
            content = new Pane {
              children = {
                if (paused) {
                  ViewFX.renderMenu()
                } else {
                  ViewFX.render(state.value)
                }
              }
            }
          }
        }
      }
    }
    worldLoop(() => frame.update(frame.value + 1))
  }

}
