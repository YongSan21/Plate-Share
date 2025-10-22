package san

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.Includes.*
import san.service.Router
import san.util.Database
import san.db.UserRepository

object MainApp extends JFXApp3:
  override def start(): Unit =
    Database.setupDB()
    UserRepository.seedIfEmpty()
    
    val rootLoader = new FXMLLoader(getClass.getResource("/san/view/RootLayout.fxml"))
    val rootLayout = rootLoader.load[javafx.scene.layout.BorderPane]()

    // init router AFTER root is loaded
    Router.init(rootLayout)
    Router.showCenter("/san/view/Welcome.fxml")

    stage = new PrimaryStage():
      title = "Plate Share"
      width = 920
      height = 730
      icons += new Image(getClass.getResourceAsStream("/images/plate_share_logo.png"))
      scene = new Scene():            // ScalaFX scene
        root = rootLayout             // works because of Includes._

    // add CSS via ScalaFX property (not getScene)
    stage.scene().stylesheets += getClass.getResource("/css/app.css").toExternalForm
