package spaceinvaders42

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color._

object SnakeFx extends JFXApp3 {

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      width = 960
      height = 540
      scene = new Scene {
        fill = White
      }
    }
  }

}
