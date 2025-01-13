package spaceinvaders42

abstract class Projectile extends Entity with View {
  def move(board: Board): Option[Projectile]
}
