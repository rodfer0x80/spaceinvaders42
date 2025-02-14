package spaceinvaders42

import cats.effect.*
import javax.sound.sampled.{AudioSystem, Clip}

object Audio {
  sealed trait AudioError extends Exception
  case class ResourceNotFound(message: String) extends AudioError
  case class PlaybackError(message: String) extends AudioError
  case class AudioPath(value: String) extends AnyVal

  trait Player {
    def play: IO[Unit]
    def loop: IO[Unit]
    def stop: IO[Unit]
  }

  class ClipPlayer(clip: Clip) extends Player {
    def play: IO[Unit] = IO(clip.start())
    def loop: IO[Unit] = IO(clip.loop(Clip.LOOP_CONTINUOUSLY))
    def stop: IO[Unit] = IO(clip.stop())
    def resume: IO[Unit] = IO {
      if (!clip.isRunning && clip.getFramePosition < clip.getFrameLength) {
        clip.start()
      }
    }
  }

  object Player {
    def load(path: AudioPath): Resource[IO, Player] = {
      def acquire: IO[Clip] = for {
        url <- IO(getClass.getResource(path.value))
          .flatMap(opt =>
            Option(opt).fold(
              IO.raiseError[java.net.URL](
                ResourceNotFound(s"Audio file not found: ${path.value}")
              )
            )(IO.pure)
          )
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

  def playLoopAudio(audioPath: AudioPath): IO[Unit] = {
    Player
      .load(audioPath)
      .use { player =>
        player.loop >>
          IO.never
      }
      .handleErrorWith {
        case e: AudioError =>
          IO.println(s"Audio playback error: ${e.getMessage}")
        case e: Exception =>
          IO.println(s"Unexpected error during audio playback: ${e.getMessage}")
      }
  }

  def playAudio(audioPath: AudioPath): IO[Unit] = {
    Player
      .load(audioPath)
      .use { player =>
        for {
          _ <- IO.println("Sound loaded, starting playback...") >> player.play
          _ <- IO.blocking(Thread.sleep(1337)) // Wait for the sound to finish
          _ <- IO.println("Sound playback finished")
        } yield ()
      }
      .handleErrorWith {
        case e: AudioError =>
          IO.println(s"Sound effect error: ${e.getMessage}")
        case e: Exception =>
          IO.println(s"Unexpected error playing sound: ${e.getMessage}")
      }
  }
}

