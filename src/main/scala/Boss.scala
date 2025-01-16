package spaceinvaders42

import scala.util.Random

case class Boss(
    x: Int,
    y: Int,
    width: Int = 100,
    height: Int = 100,
    speed: Int = 20,
    resource: String = "Orange",
    bullets: List[Bullet] = Nil
) extends Enemy {
  val rand: Random = new Random

  override def action(board: Board): Boss = {
    val xD = if (rand.nextBoolean()) 1 else -1
    val yD = if (rand.nextBoolean()) 1 else -1
    val updatedBoss: Boss = Boss(x = x + xD * speed, y = y + yD * speed, speed = speed)
    val finalUpdatedBoss: Boss = {
      if (updatedBoss.collidesWithBorder(board.border)) {
        val updatedX = if (updatedBoss.collidesWith(board.border.left) || updatedBoss.collidesWith(board.border.right)) -xD * speed else x + xD * speed
        val updatedY = if (updatedBoss.collidesWith(board.border.top) || updatedBoss.collidesWith(board.border.bottom)) -yD * speed else y + yD * speed
        Boss(x = updatedX, y = updatedY, speed = -speed)
      } else {
        updatedBoss
      }
    }
    finalUpdatedBoss
  }

}
