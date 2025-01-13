package spaceinvaders42

case class Bullet(
    x: Int,
    y: Int,
    width: Int = 5,
    height: Int = 15,
    speed: Int = 20,
    resource: String = "Blue"
) extends Projectile {
  override def move(board: Board): Option[Bullet] = {
    val updatedBullet: Bullet = Bullet(x, y - speed)
    if (!updatedBullet.collidesWithBorder(board.border)) Some(updatedBullet)
    else None
  }
}
