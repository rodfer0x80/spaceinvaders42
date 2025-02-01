package spaceinvaders42

import scalafx.scene.Node

case class MenuBackground(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    resource: String
) extends View {}

case class MenuFont(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    resource: String,
    textContent: String,
    fontSize: Int
) extends View {
  override def render() = renderFont(textContent, fontSize)
}


object Menu {
  val x: Int = 0
  val y: Int = 0
  val width: Int = 1000
  val height: Int = 1000
  val menuBackground: MenuBackground = MenuBackground(
    x = x,
    y = y,
    width = width,
    height = height,
    resource = "Blue"
  )
  val pressEnterToPlay: MenuFont = MenuFont(
    x = 200,
    y = 800,
    width = 100,
    height = 50,
    resource = "White",
    textContent = "<press enter to play>",
    fontSize = 20
  )

  def render(): List[Node] = {
    val menuBackgroundView = menuBackground.render()
    val pressEnterToPlayView = pressEnterToPlay.render()
    List(menuBackgroundView, pressEnterToPlayView)
  }

}

