package spaceinvaders42

abstract class Enemy extends Entity with View {
  val bullets: List[Bullet]

  def action(): Enemy
}
