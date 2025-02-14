package spaceinvaders42

abstract class Enemy extends Entity with View {
  val bullets: List[Bullet]

  def action(): Enemy
  
  def copy(bulletsCopy: List[Bullet]): Enemy

  def updateBullets(): List[Bullet] = {
    bullets
      .map(_.moveDown())
      .collect { case Some(bullet) =>
        bullet
      }
  }

}
