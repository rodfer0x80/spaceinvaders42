package spaceinvaders42

abstract class Enemy extends Entity with View {
  val bullets: List[Bullet]

  def action(): Enemy

  def updateBullets(): List[Bullet] = {
    bullets
      .map(_.move())
      .collect { case Some(bullet) =>
        bullet
      }
  }
}
