package san.view

import javafx.fxml.FXML
import javafx.scene.control.{Alert, ButtonType}
import javafx.scene.control.Alert.AlertType
import san.service.{Router, Session, CartService}

class NavBarController {
  @FXML private var navBar: javafx.scene.control.ToolBar = _ // Main navigation toolbar
  @FXML private var homeBtn: javafx.scene.control.Button = _ // Home button
  @FXML private var donateBtn: javafx.scene.control.Button = _ // Donate button
  @FXML private var historyBtn: javafx.scene.control.Button = _ // History button
  @FXML private var logoutBtn: javafx.scene.control.Button = _ // Logout button

  @FXML private def initialize(): Unit = {
    // Hide the entire toolbar until logged in
    navBar.visibleProperty().bind(Session.loggedIn)
    navBar.managedProperty().bind(navBar.visibleProperty()) // remove layout space when hidden
  }

  // Confirm and end user session
  @FXML private def logout(): Unit = {
    val alert = new Alert(AlertType.CONFIRMATION)
    alert.setTitle("Logout")
    alert.setHeaderText("Log out from Plate Share?")
    alert.setContentText("You’ll need to log in again to continue donating.")
    val ok = alert.showAndWait()
    // Only proceed if user confirms logout
    if (ok.isPresent && ok.get == ButtonType.OK) {
      CartService.clear()
      Session.setUser(None)
      // Return to login page
      Router.showCenter("/san/view/Login.fxml")
    }
  }

  @FXML def goHome(): Unit   = Router.showCenter("/san/view/Home.fxml") // Navigate to home page
  @FXML def goDonate(): Unit = Router.showCenter("/san/view/DonateFood.fxml") // Navigate to donate food page
  @FXML def goHistory(): Unit= Router.showCenter("/san/view/History.fxml") // Navigate to history page 
}
