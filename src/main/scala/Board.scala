package spaceinvaders42

case class RightBorder(
    x: Int,
    y: Int,
    height: Int,
    width: Int = 0,
    speed: Int = 0
) extends Entity
case class LeftBorder(
    x: Int,
    y: Int,
    height: Int,
    width: Int = 0,
    speed: Int = 0
) extends Entity
case class TopBorder(
    x: Int,
    y: Int,
    width: Int,
    height: Int = 0,
    speed: Int = 0
) extends Entity
case class BottomBorder(
    x: Int,
    y: Int,
    width: Int,
    height: Int = 0,
    speed: Int = 0
) extends Entity
case class Border(width: Int, height: Int) {
  val right: RightBorder = RightBorder(x = width, y = 0, height = height)
  val left: LeftBorder = LeftBorder(x = 0, y = 0, height = height)
  val bottom: BottomBorder = BottomBorder(x = 0, y = height, width = width)
  val top: TopBorder = TopBorder(x = 0, y = 0, width = width)
}

case class Board(
    x: Int = 0,
    y: Int = 0,
    width: Int = 1000,
    height: Int = 1000,
    resource: String = "/board.png"
) extends View {
  val border: Border = Border(width, height)
}
