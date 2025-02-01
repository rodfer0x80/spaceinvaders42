package spaceinvaders42

import cats.effect.*
import cats.effect.unsafe.implicits.global

object SoundFX {
  var audioFiber: Option[FiberIO[Unit]] = None

  def playLoopSound(soundFile: String): Unit = {
    audioFiber.foreach(_.cancel.unsafeRunSync())
    val fiber = Audio
      .playLoopAudio(Audio.AudioPath(soundFile))
      .onCancel(IO.println("Background music stopped"))
      .start
      .unsafeRunSync()
    audioFiber = Some(fiber)
  }

  def playSound(soundFile: String): Unit = {
    Audio
      .playAudio(Audio.AudioPath(soundFile))
      .unsafeRunAsync(result =>
        result.fold(
          error => println(s"Error playing sound: $error"),
          _ => ()
        )
      )
  }
  
  def shutdown(): Unit = {
    audioFiber.foreach(_.cancel.unsafeRunSync())
    audioFiber = None
  }
}
