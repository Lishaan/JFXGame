import scalafx.Includes._
import scalafx.scene.paint.Color
import scalafx.scene.canvas.GraphicsContext

/** A generalized superclass for all the [[Shootable]] entities. */
abstract class Ammo extends Drawable with Moveable {
	protected val _damage: Double = 5

	/** The damage of the Ammo object */
	def damage: Double = _damage
}

/** A bullet that can be shot from the position it was created. This bullet will move upwards on the game scene.
 *
 *  @constructor create a new bullet with a position
 *  @param playerPos the initial position of the bullet
 */
class Bullet (playerPos: Position) extends Ammo {
	val _position: Position = new Position(playerPos.x, playerPos.y - (Global.size("Player")/2))
	var _speed: Double = Global.speed("Bullet")
	var _size: Double  = Global.size("Bullet")
	val _color: Color = Global.color("Bullet")

	def move = { 
		speed = Global.speed("Bullet")
		size = Global.size("Bullet")
		position.moveUp(speed) 
	}

	def draw(drawer: GraphicsContext): Unit = {
		// Draws at center
		drawer.fill = color
		drawer.fillOval(position.x-size, position.y-size, size*2, size*2)
	}
}

/** A bullet that can be shot from the position it was created. This bullet will move downwards on the game scene.
 *
 *  @constructor create a new bullet with a position
 *  @param startPos the initial position of the bullet
 */
class ShooterBullet (startPos: Position) extends Ammo {
	val _position: Position = new Position(startPos.x, startPos.y + (Global.size("Shooter")/2))
	var _speed: Double = Global.speed("ShooterBullet")
	var _size: Double  = Global.size("ShooterBullet")
	val _color: Color = Global.color("ShooterBullet")

	def move = { 
		speed = Global.speed("ShooterBullet")
		size = Global.size("ShooterBullet")
		position.moveDown(speed) 
	}

	def draw(drawer: GraphicsContext): Unit = {
		// Draws at center
		drawer.fill = color
		drawer.fillOval(position.x-size, position.y-size, size*2, size*2)
	}
}