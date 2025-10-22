package san.view

import javafx.fxml.FXML
import san.service.Router

class HomeController {
  // Navigate to the food donation screen
  // Called when user clicks Start Donating button
  @FXML private def goDonate(): Unit =
    Router.showCenter("/san/view/DonateFood.fxml")
}
