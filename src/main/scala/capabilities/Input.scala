package spaceinvaders42

import scalafx.scene.input.KeyCode

import scala.collection.mutable

object Input {
  // --
  // TODO:
  // is a Set the best DS for smoothness? has some room for improvement
  // --
  val pressedKeys = mutable.Set[KeyCode]()

  def isPressed(keyCode: KeyCode): Boolean = pressedKeys.contains(keyCode)
  def keyPressed(keyCode: KeyCode): Unit = pressedKeys.add(keyCode)
  def keyReleased(keyCode: KeyCode): Unit = pressedKeys.remove(keyCode)

  def getKey: Int = {
    if (isPressed(KeyCode.W)) 1
    else if (isPressed(KeyCode.S)) 2
    else if (isPressed(KeyCode.A)) 3
    else if (isPressed(KeyCode.D)) 4
    else if (isPressed(KeyCode.Space)) 5
    else if (isPressed(KeyCode.Enter)) 11
    else 0
  }
}
