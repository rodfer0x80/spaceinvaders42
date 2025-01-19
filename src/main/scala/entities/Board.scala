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

// --
// NOTE:
// Using object fow now to reduce complexity
// Later if window scalling and changing sizes is needed will need to
// Do a class again, for now just reset everything to resize window size
// --
object Board extends View {
  val x: Int = 0
  val y: Int = 0
  val width: Int = 1000
  val height: Int = 1000
  val resource: String = "/board.png"
  val border: Border = Border(width, height)
}
