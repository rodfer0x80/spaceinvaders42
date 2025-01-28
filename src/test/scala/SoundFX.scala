import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec

class SoundFXTest extends AsyncFreeSpec with AsyncIOSpec {
  "SoundFX" - {
    "should play sound with verified effect" in {
      verifyEffectUsage {
        SoundFX.playLoopSound("background.wav").cancel
      }
    }

    "should handle sound playback" in {
      SoundFX.playSound("shoot.wav").asserting { result =>
        assert(result == ())
      }
    }
  }
}
