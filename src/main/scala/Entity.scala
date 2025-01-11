package spaceinvaders42

trait Entity {
  val x: Double
  val y: Double
  val width: Double
  val height: Double
  val speed: Double

  val rightHitbox: Double = x + width
  val leftHitbox: Double = x
  val bottomHitbox: Double = y + height
  val topHitbox: Double = y

  // TODO: should use <=/>= here or just </> ???
  def collidesWith(that: Entity): Boolean = {
    val horizontalOverlap =
      this.leftHitbox < that.rightHitbox && this.rightHitbox > that.leftHitbox
    val verticalOverlap =
      this.topHitbox < that.bottomHitbox && this.bottomHitbox > that.topHitbox
    horizontalOverlap && verticalOverlap
  }

}
