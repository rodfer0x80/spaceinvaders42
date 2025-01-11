package spaceinvaders42

case class Player(
    x: Double = 200,
    y: Double = 200,
    width: Double = 50,
    height: Double = 100,
    speed: Double = 25,
    resource: String = "/player.png",
    bullets: List[Bullet] = Nil
) extends Spaceship {
  override def action(code: Int): Player = {
    code match {
      case 1 =>
        Player(x, y - speed, width, height, speed, resource, bullets)
      case 2 =>
        Player(x, y + speed, width, height, speed, resource, bullets)
      case 3 =>
        Player(x - speed, y, width, height, speed, resource, bullets)
      case 4 =>
        Player(x + speed, y, width, height, speed, resource, bullets)
      case 5 =>
        Player(
          x,
          y,
          width,
          height,
          speed,
          resource,
          Bullet(x + width / 2, y) :: bullets
        )
      case _ => this
    }
  }

  def updateBullets(): List[Bullet] = {
    bullets
      .map(_.move())
      .collect { case Some(bullet) =>
        bullet
      }
  }
}
