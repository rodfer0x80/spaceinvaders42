package spaceinvaders42

case class Player(
    x: Int = 500,
    y: Int = 900,
    width: Int = 50,
    height: Int = 100,
    speed: Int = 25,
    resource: String = "/player.png",
    bullets: List[Bullet] = Nil
) extends Entity
    with View {
  def action(code: Int): Player = {
    code match {
      case 1 =>
        val newPlayer: Player =
          Player(x, y - speed, width, height, speed, resource, bullets)
        if (newPlayer.collidesWithBorder())
          Player(x, y, width, height, speed, resource, bullets)
        else
          newPlayer
      case 2 =>
        val newPlayer: Player =
          Player(x, y + speed, width, height, speed, resource, bullets)
        if (newPlayer.collidesWithBorder())
          Player(x, y, width, height, speed, resource, bullets)
        else
          newPlayer
      case 3 =>
        val newPlayer: Player =
          Player(x - speed, y, width, height, speed, resource, bullets)
        if (newPlayer.collidesWithBorder())
          Player(x, y, width, height, speed, resource, bullets)
        else
          newPlayer
      case 4 =>
        val newPlayer: Player =
          Player(x + speed, y, width, height, speed, resource, bullets)
        if (newPlayer.collidesWithBorder())
          Player(x, y, width, height, speed, resource, bullets)
        else
          newPlayer
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
