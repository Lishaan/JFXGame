import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

/** The main object that starts the application. */
object App extends JFXApp {
	stage = new PrimaryStage {
		title = s"${Game.name} - Main Menu"
		scene = new MainMenu(Global.gameWidth, Global.gameHeight)
		resizable = false
	}
}