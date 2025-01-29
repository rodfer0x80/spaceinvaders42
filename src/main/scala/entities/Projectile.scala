package spaceinvaders42

abstract class Projectile extends Entity with View {
  def moveUp(): Option[Projectile]
  def moveDown(): Option[Projectile]
}
