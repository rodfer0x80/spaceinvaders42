package spaceinvaders42

import scalafx.Includes.*
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.Node

trait View {
  val x: Double
  val y: Double
  val width: Double
  val height: Double
  val resource: String

  private val viewX: Double = x
  private val viewY: Double = y
  private val viewWidth: Double = width
  private val viewHeight: Double = height

  def render(): Node = {
    if (isColor(resource)) {
      val color = Color.web(resource)
      new Rectangle {
        layoutX = viewX
        layoutY = viewY
        this.width = viewWidth
        this.height = viewHeight
        fill = color
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
        fitWidth = viewWidth
        fitHeight = viewHeight
        preserveRatio = true
        layoutX = viewX
        layoutY = viewY
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
