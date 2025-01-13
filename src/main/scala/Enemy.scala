package spaceinvaders42

abstract class Enemy extends Entity with View {
  val bullets: List[Bullet]

  def action(): Enemy

  def updateBullets(board: Board): List[Bullet] = {
    bullets
      .map(_.move(board))
      .collect { case Some(bullet) =>
        bullet
      }
  }
}
