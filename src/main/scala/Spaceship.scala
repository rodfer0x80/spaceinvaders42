package spaceinvaders42

abstract class Spaceship extends Entity with View {
  val bullets: List[Bullet]

  def action(code: Int): Spaceship

  def updateBullets(board: Board): List[Bullet] = {
    bullets
      .map(_.move(board))
      .collect { case Some(bullet) =>
        bullet
      }
  }
}
