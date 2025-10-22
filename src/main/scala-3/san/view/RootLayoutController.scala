package san.view

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.{Alert, Menu, MenuBar, MenuItem}
import javafx.scene.control.Alert.AlertType
import san.service.Session

class RootLayoutController:

  @FXML private var menuBar: MenuBar = _ // Main menu bar at the top of the window
  @FXML private var fileMenu: Menu = _ // File menu containing navigation items
  @FXML private var homeItem: MenuItem = _ // Home menu item
  @FXML private var donateItem: MenuItem = _ // Donate menu item
  @FXML private var historyItem: MenuItem = _ // History menu item
  @FXML private var navController: NavBarController = _ // Reference to navigation controller

  @FXML private def initialize(): Unit =
    val locked = Session.loggedIn.not()
    homeItem.disableProperty.bind(locked)
    donateItem.disableProperty.bind(locked)
    historyItem.disableProperty.bind(locked)

  // Close the application when user select exit from menu bar
  @FXML
  def handleClose(action: ActionEvent): Unit =
    System.exit(0)

  // Display information about the Plate Share application
  @FXML
  def handleAbout(): Unit =
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("About Plate Share")
    alert.setHeaderText("Making Every Plate Count")
    alert.setContentText(
      """Plate Share is an independent initiative created to support the mission of United Nations Sustainable Development Goal 2:
        |to end hunger, ensure food security and encourage sustainable farming practices.
        |
        |Through our platform, users can explore a variety of healthy meals and contribute by donating them to individuals and communities in need.
        |We work closely with local suppliers and environmentally responsible producers to make sure every donation has a lasting impact.
        |
        |Get in Touch:
        |Email: plateshare@gmail.com
        |Phone: +60 12-345 6789
        |Address: 5, Jalan Universiti, Bandar Sunway, 47500 Petaling Jaya, Selangor
        |
        |Your support helps turn meals into hope - thank you for sharing a plate!""".stripMargin)
    alert.showAndWait()

  @FXML def goHomeFromMenu(): Unit = navController.goHome() // Navigate to home page via menu selection

  @FXML def goDonateFromMenu(): Unit = navController.goDonate() // Navigate to donate page via menu selection

  @FXML def goHistoryFromMenu(): Unit = navController.goHistory() // Navigate to history screen via menu selection



