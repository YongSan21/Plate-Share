package san.view

import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.scene.control.{TableColumn, TableView}
import san.model.Donation
import san.service.Session
import java.time.format.DateTimeFormatter
import scala.jdk.CollectionConverters.*
import javafx.collections.FXCollections
import san.db.DonationRepository

class HistoryController {
  @FXML private var table: TableView[Donation] = _ // Main table displaying donations
  @FXML private var timeColumn: TableColumn[Donation, String] = _ // Column showing donation time
  @FXML private var amountColumn: TableColumn[Donation, java.lang.Double] = _ // Column showing donation amount
  @FXML private var methodColumn: TableColumn[Donation, String] = _ // Column showing payment method
  @FXML private var donorColumn: TableColumn[Donation, String] = _ // Column showing donor name

  private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

  // Configures table columns and load donation history from database
  @FXML private def initialize(): Unit = {
    // Format LocalDataTime as readable string
    timeColumn.setCellValueFactory(cd => new ReadOnlyObjectWrapper[String](cd.getValue.timestamp.format(fmt)))
    // Extract donation amount
    amountColumn.setCellValueFactory(cd => new ReadOnlyObjectWrapper[java.lang.Double](cd.getValue.amount))
    // Get the display label for the payment method
    methodColumn.setCellValueFactory(cd => new ReadOnlyObjectWrapper[String](cd.getValue.method.label))
    // Extract username from the donor object
    donorColumn.setCellValueFactory(cd => new ReadOnlyObjectWrapper[String](cd.getValue.donor.username))

    // Load history from the database for the logged-in user
    val userId = Session.currentUser.map(_.username).getOrElse("")
    val rows = DonationRepository.findByUser(userId)
    table.setItems(FXCollections.observableArrayList(rows.asJava))
  }
}
