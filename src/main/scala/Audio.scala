package spaceinvaders42

import cats.effect.*
import cats.effect.unsafe.implicits.global

import javax.sound.sampled.{AudioSystem, Clip}

object Sink {
  sealed trait AudioError extends Exception
  case class ResourceNotFound(message: String) extends AudioError
  case class PlaybackError(message: String) extends AudioError

  case class AudioPath(value: String) extends AnyVal

  trait Player {
    def play: IO[Unit]
    def loop: IO[Unit]
    def stop: IO[Unit]
  }

  private class ClipPlayer(clip: Clip) extends Player {
    def play: IO[Unit] = IO(clip.start())
    def loop: IO[Unit] = IO(clip.loop(Clip.LOOP_CONTINUOUSLY))
    def stop: IO[Unit] = IO(clip.stop())
  }

  object Player {
    def load(path: AudioPath): Resource[IO, Player] = {
      def acquire: IO[Clip] = for {
        url <- IO(getClass.getResource(path.value))
          .flatMap(opt =>
            Option(opt).fold(
              IO.raiseError[java.net.URL](ResourceNotFound(s"Audio file not found: ${path.value}"))
            )(IO.pure))
        audioIn <- IO(AudioSystem.getAudioInputStream(url))
        clip <- IO(AudioSystem.getClip())
        _ <- IO(clip.open(audioIn))
      } yield clip

      def release(clip: Clip): IO[Unit] = IO {
        clip.stop()
        clip.close()
      }

      Resource.make(acquire)(release).map(new ClipPlayer(_))
    }
  }

  // High-level operations
  def playBackground: IO[Unit] = {
    val audioPath = AudioPath("/background.wav")

    Player.load(audioPath).use { player =>
      player.loop >>
        IO.never
    }.handleErrorWith {
      case e: AudioError =>
        IO.println(s"Audio playback error: ${e.getMessage}")
      case e: Exception =>
        IO.println(s"Unexpected error during audio playback: ${e.getMessage}")
    }
  }

  def playSound(soundPath: AudioPath): IO[Unit] = {
    Player.load(soundPath).use { player =>
      player.play
    }.handleErrorWith {
      case e: AudioError =>
        IO.println(s"Sound effect error: ${e.getMessage}")
      case e: Exception =>
        IO.println(s"Unexpected error playing sound: ${e.getMessage}")
    }
  }
}

object Audio {
  private var audioFiber: Option[FiberIO[Unit]] = None

  def startBackgroundMusic(): Unit = {
    audioFiber.foreach(_.cancel.unsafeRunSync())
    val fiber = Sink.playBackground
      .onCancel(IO.println("Background music stopped"))
      .start.unsafeRunSync()
    audioFiber = Some(fiber)
  }

  //    def playShootSound(): Unit = {
  //      Audio.playSound(Audio.AudioPath("/shoot.wav"))
  //        .unsafeRunAsyncAndForget()
  //    }

  def shutdown(): Unit = {
    audioFiber.foreach(_.cancel.unsafeRunSync())
    audioFiber = None
  }
}