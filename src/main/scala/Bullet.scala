package spaceinvaders42

case class Bullet(
    x: Double,
    y: Double,
    width: Double = 5,
    height: Double = 15,
    speed: Double = 20,
    resource: String = "Blue"
) extends Projectile {
  override def move(direction: Int): Entity = {
    direction match {
      case 1 => Bullet(x, y - speed, width, height, speed, resource)
      case 2 => Bullet(x, y + speed, width, height, speed, resource)
      case 3 => Bullet(x - speed, y, width, height, speed, resource)
      case 4 => Bullet(x + speed, y, width, height, speed, resource)
      case _ => this
    }
  }
}
