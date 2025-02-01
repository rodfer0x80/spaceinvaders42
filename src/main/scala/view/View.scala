package spaceinvaders42

import scalafx.Includes.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Node

trait View {
  val x: Int
  val y: Int
  val width: Int
  val height: Int
  val resource: String
  
  private val viewX: Int = x
  private val viewY: Int = y
  private val viewWidth: Int = width
  private val viewHeight: Int = height

  def renderFont(textContent: String, fontSize: Int): Node = {
    new Text {
      this.text = textContent
      this.font = Font("Arial", height)
      this.fill = if (isColor(resource)) Color.web(resource) else Color.Black
      this.x = viewX
      this.y = viewY
    }
  }

  def render(): Node = {
    if (isColor(resource)) {
      val color = Color.web(resource)
      new Rectangle {
        this.layoutX = viewX
        this.layoutY = viewY
        this.width = viewWidth
        this.height = viewHeight
        this.fill = color
      }
    } else {
      val image = new Image(
        getClass.getResourceAsStream(resource),
        width,
        height,
        false, // preserve ratio
        true // smooth
      )
      new ImageView(image) {
        this.fitWidth = viewWidth
        this.fitHeight = viewHeight
        this.preserveRatio = true
        this.layoutX = viewX
        this.layoutY = viewY
      }
    }
  }

  private def isColor(resource: String): Boolean = {
    try {
      Color.web(resource)
      true
    } catch {
      case _: IllegalArgumentException => false
    }
  }
}
