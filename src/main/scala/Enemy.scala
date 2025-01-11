package spaceinvaders42

abstract class Enemy extends Entity with View {
  val bullets: List[Bullet]

  override def move(direction: Int): Entity
}
