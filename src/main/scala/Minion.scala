package spaceinvaders42

case class Minion(
    x: Double,
    y: Double,
    width: Double = 20,
    height: Double = 20,
    speed: Double = 10,
    resource: String = "Red",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def move(direction: Int): Minion = {
    direction match {
      case 1 =>
        Minion(x, y - speed, width, height, speed, resource, bullets)
      case 2 =>
        Minion(x, y + speed, width, height, speed, resource, bullets)
      case 3 =>
        Minion(x - speed, y, width, height, speed, resource, bullets)
      case 4 =>
        Minion(x + speed, y, width, height, speed, resource, bullets)
      case _ => this
    }
  }

}
