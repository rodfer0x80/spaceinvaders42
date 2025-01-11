package spaceinvaders42

abstract class Spaceship extends Entity with View {
  val bullets: List[Bullet]

  def action(code: Int): Spaceship
}
