package san.service

import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.BorderPane

// Providing centralized navigation control.
// Manages content switching in the main application window's center area
object Router {
  private var root: BorderPane = _

  def init(rootPane: BorderPane): Unit = root = rootPane

  // Handles the complete navigation process
  def showCenter(fxmlPath: String): Unit = {
    val loader = new FXMLLoader(getClass.getResource(fxmlPath))
    val node: Node = loader.load()
    root.setCenter(node)
  }
}
