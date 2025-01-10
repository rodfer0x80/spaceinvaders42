package spaceinvaders42

import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.beans.property.ObjectProperty
import scalafx.beans.property.IntegerProperty

object SnakeFx extends JFXApp3 {

  def gameLoop(update: () => Unit): Unit =
    Future {
      update()
      Thread.sleep(1000 / 25 * 2)
    }.flatMap(_ => Future(gameLoop(update)))

  val initSnake: List[(Double, Double)] = List(
    (250, 200),
    (225, 200),
    (200, 200)
  )

  case class State(snake: List[(Double, Double)], food: (Double, Double)) {
    def newState(d: Int): State = {
      val (x: Double, y: Double) = snake.head
      val (newx: Double, newy: Double) = d match {
        case 1 => (x, y - 25)
        case 2 => (x, y + 25)
        case 3 => (x - 25, y)
        case 4 => (x + 25, y)
        case _ => (x, y)
      }

      val newSnake: List[(Double, Double)] = {
        if (
          newx < 0 || newx >= 600 || newy < 0 || newy >= 600 || snake.tail
            .contains(
              (newx, newy)
            )
        )
          initSnake
        else if (food == (newx, newy))
          food :: snake
        else
          (newx, newy) :: snake.init
      }

      val newFood: (Double, Double) =
        if (food == (newx, newy))
          randgenewFood()
        else
          food

      State(newSnake, newFood)
    }

    def rectangles: List[Rectangle] =
      rect(food._1, food._2, Color.Red) :: snake.map { case (x, y) =>
        rect(x, y, Color.LimeGreen)
      }
  }

  def randgenewFood(): (Double, Double) = {
    (Random.nextInt(600 / 25) * 25, Random.nextInt(600 / 25) * 25)
  }

  def rect(xr: Double, yr: Double, color: Color): Rectangle = new Rectangle {
    x = xr
    y = yr
    width = 25
    height = 25
    fill = color
  }

  override def start(): Unit = {
    val state = ObjectProperty(State(initSnake, randgenewFood()))
    val frame = IntegerProperty(0)
    val d = IntegerProperty(4) // right

    frame.onChange {
      state.update(state.value.newState(d.value))
    }

    stage = new JFXApp3.PrimaryStage {
      width = 600
      height = 600
      scene = new Scene {
        fill = Color.Grey
        content = state.value.rectangles
        onKeyPressed = key =>
          key.getText match {
            case "w" => d.value = 1
            case "s" => d.value = 2
            case "a" => d.value = 3
            case "d" => d.value = 4
          }

        frame.onChange {
          Platform.runLater {
            content = state.value.rectangles
          }
        }

      }
    }

    gameLoop(() => frame.update(frame.value + 1))
  }

}
