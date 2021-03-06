import scala.collection.mutable.{Map => MMap, ArrayBuffer}
import scala.collection.immutable.Map
import scalafx.Includes._
import scalafx.scene.paint.Color

/** A Score object that stores a score.
 *
 *  @constructor create a new instance of a score object
 *  @param name name of the scorer
 *  @param kills the total kills of the scorer
 *  @param score the total score of the scorer
 */
case class Score (name: String, kills: Int, score: Double)

/** A Health object that is used to store Health for all the Damageable entities.
 *
 *  @constructor create a new instance of a Health object
 *  @param max the total health of the entity
 */
case class Health (max: Double) { 
	var current: Double = max 
	
	/** 
	 *  @return the current percentage of the health as a Double
	 */
	def percentage: Double = current / max
}

/** A Position object that stores the (x, y) coordinates of a Moveable entity.
 *
 *  @constructor create a new instance of a Position object
 *  @param x the initial x coordinate
 *  @param y the initial y coordinate
 */
case class Position (var x: Double, var y: Double) {
	def moveUp    (speed: Double): Unit = { y = y - speed * Global.delta }
	def moveRight (speed: Double): Unit = { x = x + speed * Global.delta }
	def moveDown  (speed: Double): Unit = { y = y + speed * Global.delta }
	def moveLeft  (speed: Double): Unit = { x = x - speed * Global.delta }
}

/** A Velocity object that stores the speed of a Moveable entity in a given direction by the (x, y) coordinates.
 *
 *  @constructor create a new instance of a Velocity object
 *  @param speed the speed of the Moveable object
 */
case class Velocity (var speed: Double) {
	var x: Double = if (math.random*2 >= 1) speed else -speed
	var y: Double = if (math.random*2 <= 1) speed else -speed
}

/** A Spawner object that spawns an enemy by the given enemyName when the delay given is stopped.
 *
 *  @constructor create a new instance of a Spawner object that spawns at a range of delay
 *  @param enemyName the name of the enemy that needs to be spawn
 *  @param delayHead the starting range of the delay in seconds
 *  @param delayTail the ending range of the delay in seconds
 */
class Spawner (val enemyName: String, val delayHead: Double, val delayTail: Double) {

	/** create a new instance of a Spawner object that spawns at a consistent delay
	 *  @param enemyName the name of the enemy that needs to be spawn
	 *  @param delay the consistent delay in seconds
	 */
	def this(enemyName: String, delay: Double) = this(enemyName, delay, delay)

	private val _random: scala.util.Random = new scala.util.Random

	/** Returns a random value between the delayHead and the delayTail */
	def random: Double = (delayHead+_random.nextInt((delayTail-delayHead).toInt+1))

	/** The delay counter of the [[Spawner]] object */
	var counter: Double = random
	
	/** Returns a boolean value that determines whether the counter has been stopped */
	def stopped: Boolean = (counter <= 0)

	/** Resets the counter to a random value between the delayHead and delatTail */
	def reset: Unit = { counter = random }

	/** Updates the current counter of the spawner by the global delta time */
	def update: Unit = { counter -= Global.delta }
}

/** A Global static object that stores all the variables that change in the game every iteration of the game loop. */
object Global {
	/** The current player [[Position]] */
	var playerPos: Position = new Position(Global.gameWidth/2, Global.gameHeight-50)

	/** The current delta time difference */
	var delta: Double = 0

	/** The seconds surpassed of the current [[Game]] */
	var seconds: Double = 0

	/** The scale of the game */
	var gameScale: Double = 1.2

	/** The speed of the game */
	var gameSpeed: Double = 1.0

	/** The width of the game scene */
	val gameWidth: Double = 600
	/** The height of the game scene */
	val gameHeight: Double = 600

	/** The size of the play area */
	val playAreaHeight: Double = Global.gameHeight/1.5

	/** Whether the current game should append to the highscore when finished */
	var appendToHighscoresFile: Boolean = true

	/** The intiial speeds of the Moveable entities in the game */
	val SPEED: MMap[String, Double] = MMap (
		"Player"         -> 170,
		"Bullet"         -> 400,
		"Seeker"         -> 80,
		"Bouncer"        -> 120,
		"Shooter"        -> 70,
		"ShooterBullet"  -> 300
	)

	/** The intiial sizes of the Moveable entities in the game */
	val SIZE: MMap[String, Double] = MMap (
		"Player"         -> 20,
		"Bullet"         -> 4,
		"Seeker"         -> 10,
		"Bouncer"        -> 17,
		"Shooter"        -> 30,
		"ShooterBullet"  -> 6
	)

	/** The colors of all the Drawable entities in the game */
	val color: Map[String, Color] = Map (
		"Background"    -> Color.web("041A1A"),
		"PlayArea"      -> Color.web("173B3B"),
		"TimerText"     -> Color.web("FBFBFB"),
		"PausedText"    -> Color.web("FBFBFB"),
		"Player"        -> Color.web("FBFBFB"),
		"Bullet"        -> Color.web("44f9ff"),
		"ShooterBullet" -> Color.web("F7E8D0"),
		"Seeker"        -> Color.web("6F997A"),
		"Bouncer"       -> Color.web("79678A"),
		"Shooter"       -> Color.web("856D48")
		/* Link: https://coolors.co/0c0910-cdd1c4-5c80bc-6b2737-1d7874 */
	)

	/** The health of all the Damageable entities */
	val health: Map[String, Double] = Map (
		"Seeker"  -> 15,
		"Bouncer" -> 60,
		"Shooter" -> 100
	)

	/** The adjusted speeds of the Moveable entities in the game according to the current game's status */
	val speed: MMap[String, Double] = MMap (
		"Player"         -> Global.gameSpeed*SPEED("Player"),
		"Bullet"         -> Global.gameSpeed*SPEED("Bullet"),
		"Seeker"         -> Global.gameSpeed*SPEED("Seeker"),
		"Bouncer"        -> Global.gameSpeed*SPEED("Bouncer"),
		"Shooter"        -> Global.gameSpeed*SPEED("Shooter"),
		"ShooterBullet"  -> Global.gameSpeed*SPEED("ShooterBullet")
	)

	/** The adjusted sizes of the Moveable entities in the game according to the current game's status */
	val size: MMap[String, Double] = MMap (
		"Player"         -> Global.gameScale*SIZE("Player"),
		"Bullet"         -> Global.gameScale*SIZE("Bullet"),
		"Seeker"         -> Global.gameScale*SIZE("Seeker"),
		"Bouncer"        -> Global.gameScale*SIZE("Bouncer"),
		"Shooter"        -> Global.gameScale*SIZE("Shooter"),
		"ShooterBullet"  -> Global.gameScale*SIZE("ShooterBullet")
	)

	/** Updates all the sizes and the speeds of Moveable entities in the game according to the current status of the game. */
	def updateStats: Unit = {
		Global.speed("Player" ) = Global.gameSpeed*SPEED("Player")
		Global.speed("Bullet" ) = Global.gameSpeed*SPEED("Bullet")
		Global.speed("Seeker" ) = Global.gameSpeed*SPEED("Seeker")
		Global.speed("Bouncer") = Global.gameSpeed*SPEED("Bouncer")
		Global.speed("Shooter") = Global.gameSpeed*SPEED("Shooter")
		Global.speed("ShooterBullet" ) = Global.gameSpeed*SPEED("ShooterBullet")

		Global.size("Player" ) = Global.gameScale*SIZE("Player")
		Global.size("Bullet" ) = Global.gameScale*SIZE("Bullet")
		Global.size("Seeker" ) = Global.gameScale*SIZE("Seeker")
		Global.size("Bouncer") = Global.gameScale*SIZE("Bouncer")
		Global.size("Shooter") = Global.gameScale*SIZE("Shooter")
		Global.size("ShooterBullet" ) = Global.gameScale*SIZE("ShooterBullet")
	}
}