import scala.collection.mutable.ArrayBuffer
import scalafx.Includes._
import scalafx.scene.paint.Color
import scalafx.scene.canvas.GraphicsContext

abstract class Enemy extends Drawable with Moveable with Damageable
object Enemy {
	def spawn(enemyType: String): Enemy = {
		if (enemyType.equals("Seeker")) {
			return new Seeker
		} else if (enemyType.equals("Bouncer")) {
			return new Bouncer
		} else if (enemyType.equals("Shooter")) {
			return new Shooter
		} else {
			println(s"Warning: Enemy $enemyType cannot be not found, Seeker spawned instead")
			return new Seeker
		}
	}
}

class Seeker extends Enemy {
	val _position: Position = new Position(math.random*Const.gameWidth, 0)
	var _speed: Double = Const.speed("Seeker")
	var _size: Double = Const.size("Seeker")
	val _color: Color = Const.color("Seeker")
	val _health: Health = Health(Const.health("Seeker"))

	def move = {
		speed = Const.speed("Seeker")
		size = Const.size("Seeker")

		val playerPos: Position = Global.playerPos

		val dx: Double = playerPos.x-position.x
		val dy: Double = playerPos.y-position.y

		val dist: Double = math.sqrt(dx*dx + dy*dy)

		position.x = position.x + dx / dist * speed * Global.delta
		position.y = position.y + dy / dist * speed * Global.delta
	}

	def draw(drawer: GraphicsContext): Unit = {
		// Draws at center
		drawer.fill = color
		drawer.fillOval(position.x-size, position.y-size, size*2, size*2)

		// Health Bar
		val x = position.x-size
		val y = position.y+size+(size/2)
		val w = size*2
		val h = size/4

		drawer.fill = Color.Blue
		drawer.fillRect(x, y, w, h)

		drawer.fill = Color.Red
		drawer.fillRect(x, y, w*health.percentage, h)
	}
}

class Bouncer extends Enemy {
	val _position: Position = new Position(Const.gameWidth/2, Const.playAreaHeight/2)
	var _speed: Double = Const.speed("Bouncer")
	var _size: Double = Const.size("Bouncer")
	val _color: Color = Const.color("Bouncer")
	val _health: Health = Health(Const.health("Bouncer"))

	private val _velocity: Velocity = new Velocity(speed)

	def velocity: Velocity = _velocity

	def move = {
		speed = Const.speed("Bouncer")
		size = Const.size("Bouncer")
		position.x = position.x + velocity.x * Global.delta
		position.y = position.y + velocity.y * Global.delta

		if (position.x < 0+size || position.x > Const.gameWidth-size) {
			velocity.x = -velocity.x
		}

		if (position.y < 0+size || position.y > Const.gameHeight-size) {
			velocity.y = -velocity.y
		}
	}

	def draw(drawer: GraphicsContext): Unit = {
		// Draws at center
		drawer.fill = color
		drawer.fillOval(position.x-size, position.y-size, size*2, size*2)

		// Health Bar
		val x = position.x-size
		val y = position.y+size+(size/2)
		val w = size*2
		val h = size/4

		drawer.fill = Color.Blue
		drawer.fillRect(x, y, w, h)

		drawer.fill = Color.Red
		drawer.fillRect(x, y, w*health.percentage, h)
	}
}

class Shooter extends Enemy with Shootable {
	private var _rotation: Double = 0
	private var _rotationSpeed: Double = Const.speed("Shooter")*0.60
	private var _rotationRadius: Double = size*4

	private val _rotationPos: Position = new Position(math.random*Const.gameWidth, Const.playAreaHeight/2)
	val _position: Position = new Position(
		size*math.cos(_rotationRadius)+_rotationPos.x, 
		size*math.sin(_rotationRadius)+_rotationPos.y
	)

	var _speed: Double = Const.speed("Shooter")
	var _size: Double = Const.size("Shooter")
	val _color: Color = Const.color("Shooter")
	val _health: Health = Health(Const.health("Shooter"))

	private var dir: Int = 0

	def shootBullet: Unit = {
		if (Global.seconds.toInt % 2 == 0 && bullets.length < 1) {
			val currentPlayerPos = Global.playerPos
			_bullets +:= new ShooterBullet(this.position)
		}
	}

	def move = {
		speed = Const.speed("Shooter")
		size = Const.size("Shooter")

		position.x = size*math.cos(_rotationRadius)+_rotationPos.x + speed * Global.delta
		position.y = size*math.sin(_rotationRadius)+_rotationPos.y + speed * Global.delta

		_rotationRadius += 0.03
		if (_rotationRadius > (2*math.Pi)) {
			_rotationRadius = 0
		}

		dir match {
			case 0 => _rotationPos.x = _rotationPos.x + speed * Global.delta
			case 1 => _rotationPos.x = _rotationPos.x - speed * Global.delta
			case _ =>
		}

		// Change direction
		if (_rotationPos.x > Const.gameWidth-size) {
			this.dir = 1
		} else if (_rotationPos.x < size) {
			this.dir = 0
		}
	}

	def draw(drawer: GraphicsContext): Unit = {
		// Draws at center
		drawer.fill = color
		drawer.fillOval(position.x-size, position.y-size, size*2, size*2)

		// Health Bar
		val x = position.x-size
		val y = position.y+size+(size/2)
		val w = size*2
		val h = size/4

		drawer.fill = Color.Blue
		drawer.fillRect(x, y, w, h)

		drawer.fill = Color.Red
		drawer.fillRect(x, y, w*health.percentage, h)

		// Bullets
		bullets.foreach(b => b.draw(drawer))
	}
}

trait Shootable {
	protected var _bullets: ArrayBuffer[Ammo] = ArrayBuffer()
	def bullets: ArrayBuffer[Ammo] = _bullets

	def updateBullets: Unit = {
		// Bullets
		if (!bullets.isEmpty) {
			var indexes: ArrayBuffer[Int] = ArrayBuffer()

			// Bullets move
			for (i <- 0 until bullets.length) {
				bullets(i).move
				if (bullets(i).y > Const.gameHeight-bullets(i).size || bullets(i).y < bullets(i).size) {
					if (!indexes.contains(i)) indexes += i
				}
			}

			// Bullets Buffer
			indexes.foreach(index => bullets.remove(index))
		}
	}

	def shootBullet: Unit
}

trait Drawable {
	protected val _color: Color
	def color: Color = _color

	def draw(drawer: GraphicsContext): Unit
}

trait Moveable {
	protected val _position: Position
	protected var _speed: Double
	protected var _size: Double
	
	def position: Position = _position
	def speed: Double = _speed
	def size: Double = _size
	
	def x: Double = _position.x
	def y: Double = _position.y

	def speed_=(speed: Double) = { _speed = speed }
	def size_=(size: Double) = { _size = size }

	def move: Unit
	def remove: Unit = { _position.x = -800 }
}

trait Damageable {
	protected val _health: Health

	def dead: Boolean = (_health.current <= 0)
	def alive: Boolean = !dead
	def health: Health = _health
	def inflictDamage(damage: Double): Unit = { _health.current = _health.current - damage }
}