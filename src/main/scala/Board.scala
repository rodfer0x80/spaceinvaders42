package spaceinvaders42

case class Board(
    x: Double = 0,
    y: Double = 0,
    width: Double = 1000,
    height: Double = 1000,
    resource: String = "/board.png"
) extends View
