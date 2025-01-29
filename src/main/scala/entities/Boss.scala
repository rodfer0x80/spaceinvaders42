package spaceinvaders42

import scala.util.Random

case class Boss(
    x: Int,
    y: Int,
    width: Int = 100,
    height: Int = 100,
    speed: Int = 20,
    resource: String = "/boss.png",
    bullets: List[Bullet] = Nil
) extends Enemy {
  val rand: Random = new Random

  override def copy(bulletsCopy: List[Bullet]): Boss = Boss(
    x = this.x,
    y = this.y,
    width = this.width,
    height = this.height,
    speed = this.speed,
    resource = this.resource,
    bullets = bulletsCopy
  )

  override def action(): Boss = {
    val xD = if (rand.nextBoolean()) 1 else -1
    val yD = if (rand.nextBoolean()) 1 else -1
    val updatedBullets: List[Bullet] =
      if (rand.nextBoolean())
        Bullet(this.x + this.width / 2, this.y) :: updateBullets()
      else updateBullets()
    val updatedBoss: Boss =
      Boss(
        x = this.x + xD * this.speed,
        y = y + yD * this.speed,
        speed = this.speed,
        bullets = updatedBullets
      )
    val finalUpdatedBoss: Boss = {
      if (updatedBoss.collidesWithBorder()) {
        val updatedX =
          if (
            updatedBoss.collidesWith(Board.border.left) || updatedBoss
              .collidesWith(Board.border.right)
          ) -xD * this.speed
          else x + xD * this.speed
        val updatedY =
          if (
            updatedBoss.collidesWith(Board.border.top) || updatedBoss
              .collidesWith(Board.border.bottom)
          ) -yD * this.speed
          else y + yD * this.speed
        Boss(
          x = updatedX,
          y = updatedY,
          speed = -this.speed,
          bullets = updatedBullets
        )
      } else {
        updatedBoss
      }
    }
    finalUpdatedBoss
  }

}
