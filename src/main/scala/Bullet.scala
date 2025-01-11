package spaceinvaders42

case class Bullet(
    x: Double,
    y: Double,
    width: Double = 5,
    height: Double = 15,
    speed: Double = 20,
    resource: String = "Blue"
) extends Projectile {
  override def move(): Option[Bullet] = {
    val updatedY: Double = y - speed
    if (updatedY > 0) Some(Bullet(x, updatedY)) else None
  }
}
