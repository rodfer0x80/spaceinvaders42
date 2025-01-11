package spaceinvaders42

case class Boss(
    x: Double,
    y: Double,
    width: Double = 60,
    height: Double = 60,
    speed: Double = 10,
    resource: String = "Orange",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def move(direction: Int): Boss = {
    direction match {
      case 1 =>
        Boss(x, y - speed, width, height, speed, resource, bullets)
      case 2 =>
        Boss(x, y + speed, width, height, speed, resource, bullets)
      case 3 =>
        Boss(x - speed, y, width, height, speed, resource, bullets)
      case 4 =>
        Boss(x + speed, y, width, height, speed, resource, bullets)
      case _ => this
    }
  }
}
