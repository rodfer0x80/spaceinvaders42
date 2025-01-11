package spaceinvaders42

abstract class Spaceship extends Entity with View {

  val bullets: List[Bullet]

  override def move(direction: Int): Entity
}
