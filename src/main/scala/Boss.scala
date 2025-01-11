package spaceinvaders42

case class Boss(
    x: Double,
    y: Double,
    width: Double = 60,
    height: Double = 60,
    speed: Double = 10,
    resource: String = "Orange",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def action(): Boss = this
}
