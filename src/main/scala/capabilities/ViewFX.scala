package spaceinvaders42

import scalafx.scene.Node

object ViewFX {
  def render(state: State): List[Node] = {
    val boardView = Board.render()
    val playerView = state.player.render()
    val playerBulletsView = state.player.bullets.map { bullet =>
      bullet.render()
    }
    val playerViews = playerView :: playerBulletsView
    val enemiesViews = state.enemies.flatMap { enemy =>
      val enemyView = enemy.render()
      val enemyBulletsView = enemy.bullets.map { bullet =>
        bullet.render()
      }
      enemyView :: enemyBulletsView
    }
    boardView :: playerViews ::: enemiesViews
  }

}
