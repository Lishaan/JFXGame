import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Bounds
import scalafx.stage.Stage
import scalafx.scene.control.{Button, ListView}
import scalafx.scene.layout.BorderPane
import scalafx.scene.{Node, Scene}
import scalafx.event.ActionEvent
import scalafx.scene.text.Text
import scalafx.scene.shape.Circle
import scalafx.scene.input.{KeyEvent, KeyCode}
import scalafx.event.ActionEvent
import scalafx.animation.AnimationTimer
import scalafx.scene.paint.Color
import scalafx.collections.ObservableBuffer
import scala.collection.mutable.{ArrayBuffer, Map}

class Game (val playerName: String) extends Stage {

	def this() = this("Player")
	def closeGame() = this.close

	title = "JFXGame - Play"
	resizable = false

	scene = new Scene(Const.gameWidth, Const.gameHeight) {
		fill = Const.color("Background")

		var healthTexts: ArrayBuffer[HealthText] = ArrayBuffer()
		var enemies: ArrayBuffer[Enemy] = ArrayBuffer()
		var bullets: ArrayBuffer[Bullet] = ArrayBuffer()
		
		val player = new Player(playerName)
		var timerText = new Text(10, 20, "0.0") {
			fill = Const.color("TimerText")
		}

		content = List(player.shape, timerText)

		// Memory Allocations for Stage.content
		val bulletsCounter = BufferCounter(Const.memory("Bullets").head, Const.memory("Bullets").tail)
		for (i <- Const.memory("Bullets").head to Const.memory("Bullets").tail) { content += new Circle() }
		
		val enemiesCounter = BufferCounter(Const.memory("Enemies").head, Const.memory("Enemies").tail)
		for (i <- Const.memory("Enemies").head to Const.memory("Enemies").tail) { content += new Circle() }

		val healthTextCounter = BufferCounter(Const.memory("HealthText").head, Const.memory("HealthText").tail)
		for (i <- Const.memory("HealthText").head to Const.memory("HealthText").tail) { content += new Text() }

		var keys = Map(
			"Right" -> false,
			"Left"  -> false
		)

		onKeyPressed = (e: KeyEvent) => {
			e.code match {
				case KeyCode.Right => keys("Right") = true
				case KeyCode.Left => keys("Left") = true
				case _ => 
			}
		}

		onKeyReleased = (e: KeyEvent) => {
			e.code match {
				case KeyCode.Right => keys("Right") = false
				case KeyCode.Left => keys("Left") = false
				case KeyCode.Space => {
					bullets +:= new Bullet(player.pos)
					content(bulletsCounter.value) = bullets.head
					bulletsCounter.increment
				}
				case _ =>
			}
		}

		var lastTime = -3L
		var spawnDelay = 1.0
		var seconds = 0.0

		val timer: AnimationTimer = AnimationTimer(t => {
			if(lastTime > 0) {
				val delta = (t-lastTime)/1e9
				
				Global.playerPos = player.pos
				Global.delta = delta

				var indexes: ArrayBuffer[Int] = ArrayBuffer()

				// Enemies
				if (!enemies.isEmpty) {
					indexes = ArrayBuffer()
					for (i <- 0 until enemies.length) {
						// Player death
						if (enemies(i).intersects(player.bounds)) {
							timer.stop
							
							// TODO: Write ${player.kills} and $seconds to highscore
							// player.kills
							// seconds

							root = new BorderPane {
								center = new Button("Go Back To Main Menu") {
									onAction = (e: ActionEvent) => closeGame()
								}
							}
						}

						// Enemies & Bullets
						bullets.foreach(bullet => {
							if (bullet.intersects(enemies(i).bounds)) {
								enemies(i).inflictDamage(bullet.damage)
								bullet.remove
								
								if (enemies(i).dead) {
									enemies(i).remove
									healthTexts(i).remove
									player.incrementKills
									if (!indexes.contains(i)) indexes += i
								}
							}
						})

						// Enemies move
						enemies(i).move
						healthTexts(i).update(enemies(i).healthObject, enemies(i).pos)
					}

					// Enemies Buffer
					indexes = indexes.distinct
					for (i <- 0 until indexes.length) {
						enemies.remove(indexes(i))
						healthTexts.remove(indexes(i))
					}
				}

				// Bullets
				if (!bullets.isEmpty) {
					indexes = ArrayBuffer()

					// Bullets move
					for (i <- 0 until bullets.length) {
						bullets(i).move
						if (bullets(i).y < (-bullets(i).r)) {
							indexes += i
						}
					}

					// Bullets Buffer
					indexes = indexes.distinct
					for (i <- 0 until indexes.length) {
						bullets.remove(indexes(i))
					}
				}

				// Player move
				if (keys("Right")) player.move("Right", delta)
				if (keys("Left")) player.move("Left", delta)

				// Enemies Spawn
				spawnDelay -= delta
				seconds += delta
				if (spawnDelay < 0) {
					val enemy = Enemy.spawn("Seeker")
					content(enemiesCounter.value) = enemy.shape
					enemiesCounter.increment
					enemies +:= enemy

					val healthText = Enemy.drawHealth(enemy.healthObject, enemy.pos)
					content(healthTextCounter.value) = healthText
					healthTextCounter.increment
					healthTexts +:= healthText

					spawnDelay = 1.0
				}

				// DEBUG
				// println(content.length)
				// var count = 0
				// for (i <- content) {
				// 	println(s"DEBUG: $count ${i.toString}")
				// 	count += 1
				// }
				// println()
				timerText.text = "%.1f".format(seconds)
			}
			lastTime = t
		})
		timer.start
	}
}