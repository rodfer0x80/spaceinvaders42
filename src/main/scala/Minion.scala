package spaceinvaders42

case class Minion(
    x: Double,
    y: Double,
    width: Double = 20,
    height: Double = 20,
    speed: Double = 10,
    resource: String = "Red",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def action(): Minion = this

}
