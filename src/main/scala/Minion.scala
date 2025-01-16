package spaceinvaders42

case class Minion(
    x: Int,
    y: Int,
    width: Int = 40,
    height: Int = 40,
    speed: Int = 10,
    resource: String = "Red",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def action(board: Board): Minion = {
    val movementHorizontalUpdatedMinion: Minion = Minion(x = x - speed, y = y, speed = speed)
    val updatedMinion: Minion = {
      if (movementHorizontalUpdatedMinion.collidesWithBorder(board.border)) {
        Minion(x = x - speed, y = y + width + speed , speed = -speed)
      } else {
        movementHorizontalUpdatedMinion
      }
    }
    updatedMinion
  }

}
