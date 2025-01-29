package spaceinvaders42

import scala.util.Random

case class Minion(
    x: Int,
    y: Int,
    width: Int = 40,
    height: Int = 40,
    speed: Int = 10,
    resource: String = "/minion.png",
    bullets: List[Bullet] = Nil
) extends Enemy {

  val rand: Random = new Random

  override def copy(bulletsCopy: List[Bullet]): Minion = Minion(
    x = this.x,
    y = this.y,
    width = this.width,
    height = this.height,
    speed = this.speed,
    resource = this.resource,
    bullets = bulletsCopy
  )

  override def action(): Minion = {
    // shoot% = 1/8
    val shoot: Boolean =
      rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean()
    val updatedBullets: List[Bullet] =
      if (shoot)
        Bullet(x + width / 2, y) :: updateBullets()
      else updateBullets()
    val movementHorizontalUpdatedMinion: Minion =
      Minion(
        x = this.x - this.speed,
        y = this.y,
        speed = this.speed,
        bullets = updatedBullets
      )
    val updatedMinion: Minion = {
      if (movementHorizontalUpdatedMinion.collidesWithBorder()) {
        Minion(
          x = this.x - this.speed,
          y = this.y + this.width + this.speed,
          speed = -this.speed,
          bullets = updatedBullets
        )
      } else {
        movementHorizontalUpdatedMinion
      }
    }
    updatedMinion
  }

}
