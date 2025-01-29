package spaceinvaders42

case class Bullet(
    x: Int,
    y: Int,
    width: Int = 5,
    height: Int = 15,
    speed: Int = 20,
    resource: String = "Pink"
) extends Projectile {
  override def moveUp(): Option[Bullet] = {
   val updatedBullet: Bullet = Bullet(x, y - speed)
    if (!updatedBullet.collidesWithBorder()) Some(updatedBullet)
    else None
  }
  override def moveDown(): Option[Bullet] = {
    val updatedBullet: Bullet = Bullet(x, y + speed)
    if (!updatedBullet.collidesWithBorder()) Some(updatedBullet)
    else None
  }
}
