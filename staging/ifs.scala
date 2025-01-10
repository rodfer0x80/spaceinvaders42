package spaceinvaders42

import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle
import scalafx.beans.value._

object Properties101 extends JFXApp3 {
  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title.value = "when/choose/otherwise"
      width = 400
      height = 300
      scene = new Scene {
        fill = Color.LightGreen
        content = new Rectangle {
          x = 25
          y = 40
          width = 100
          height = 100
          fill <== when(hover) choose Color.Green otherwise Color.Red
        }
      }
    }
  }
}
