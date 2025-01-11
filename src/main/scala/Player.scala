package spaceinvaders42

case class Player(
    x: Double = 200,
    y: Double = 200,
    width: Double = 50,
    height: Double = 100,
    speed: Double = 25,
    resource: String = "/player.png",
    bullets: List[Bullet] = Nil
) extends Spaceship {
  override def move(direction: Int): Player = {
    direction match {
      case 1 =>
        Player(x, y - speed, width, height, speed, resource, bullets)
      case 2 =>
        Player(x, y + speed, width, height, speed, resource, bullets)
      case 3 =>
        Player(x - speed, y, width, height, speed, resource, bullets)
      case 4 =>
        Player(x + speed, y, width, height, speed, resource, bullets)
      case _ => this
    }
  }

//  def update(updatedBullets: List[Bullet]) = Player(this.x, this.y, this.width, this.height, this.speed, this.resource, updatedBullets)
//
//  def moveAndUpdate(direction: Int, updatedBullets: List[Bullet]): Player = {
//    this.move(direction = direction).update(updatedBullets = updatedBullets)
// }
}
