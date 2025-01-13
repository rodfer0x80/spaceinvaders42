package spaceinvaders42

case class Boss(
    x: Int,
    y: Int,
    width: Int = 100,
    height: Int = 100,
    speed: Int = 10,
    resource: String = "Orange",
    bullets: List[Bullet] = Nil
) extends Enemy {
  override def action(): Boss = this
}
