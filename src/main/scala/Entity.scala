package spaceinvaders42

trait Entity {
  val x: Int
  val y: Int
  val width: Int
  val height: Int
  val speed: Int

  val rightHitbox: Int = x + width
  val leftHitbox: Int = x
  val bottomHitbox: Int = y + height
  val topHitbox: Int = y

  // TODO: should use <=/>= here or just </> ???
  def collidesWith(that: Entity): Boolean = {
    val horizontalOverlap =
      this.leftHitbox <= that.rightHitbox && this.rightHitbox >= that.leftHitbox
    val verticalOverlap =
      this.topHitbox <= that.bottomHitbox && this.bottomHitbox >= that.topHitbox
    horizontalOverlap && verticalOverlap
  }

  def collidesWithBorder(border: Border): Boolean = {
    this.collidesWith(border.right) ||
    this.collidesWith(border.left) ||
    this.collidesWith(border.top) ||
    this.collidesWith(border.bottom)
  }

}
