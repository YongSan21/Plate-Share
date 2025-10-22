package san.view

import javafx.fxml.FXML
import javafx.scene.control.{Alert, Button, ComboBox, Label, RadioButton, ToggleGroup}
import javafx.scene.control.Alert.AlertType
import san.model.{Donation, EWallet, OnlineBanking, PaymentMethod}
import san.service.{CartService, Router, Session}
import san.db.DonationRepository
import java.time.LocalDateTime
import java.util.UUID

class PaymentSelectionController {

  @FXML private var amountLabel: Label = _ // Label showing total amount to pay
  @FXML private var rbOnline: RadioButton = _ // Radio button for online banking
  @FXML private var rbWallet: RadioButton = _ // Radio button for e-wallet
  @FXML private var bankCombo: ComboBox[String] = _ // Dropdown for selecting bank
  @FXML private var walletCombo: ComboBox[String] = _ // Dropdown for selecting e-wallet
  @FXML private var payBtn: Button = _ // Pay now button
  @FXML private var paymentGroup: ToggleGroup = _ // Radio button group for payment methods

  @FXML private def initialize(): Unit = {
    // Block entry if cart is empty
    if (CartService.isEmpty) {
      val a = new Alert(AlertType.INFORMATION)
      a.setTitle("Nothing to pay")
      a.setHeaderText(null)
      a.setContentText("Your cart is empty. Please add items before proceeding to payment.")
      a.showAndWait()
      Router.showCenter("/san/view/DonateFood.fxml")
      return
    }

    // Add available banks and e-wallet providers
    bankCombo.getItems.setAll("Maybank", "CIMB", "RHB")
    walletCombo.getItems.setAll("Touch 'n Go", "GrabPay", "Boost")

    // Start with no method selected
    rbOnline.setSelected(false)
    rbWallet.setSelected(false)

    setBankVisible(false);  bankCombo.setDisable(true);  bankCombo.getSelectionModel.clearSelection()
    setWalletVisible(false); walletCombo.setDisable(true); walletCombo.getSelectionModel.clearSelection()

    // Listeners for method selection
    rbOnline.selectedProperty.addListener((_, _, selected: java.lang.Boolean) => {
      val now = selected.booleanValue()
      setBankVisible(now);  bankCombo.setDisable(!now)
      if (now) {
        setWalletVisible(false); walletCombo.setDisable(true)
        walletCombo.getSelectionModel.clearSelection()
      }
      updatePayEnabled()
    })

    rbWallet.selectedProperty.addListener((_, _, selected: java.lang.Boolean) => {
      val now = selected.booleanValue()
      setWalletVisible(now); walletCombo.setDisable(!now)
      if (now) {
        setBankVisible(false); bankCombo.setDisable(true)
        bankCombo.getSelectionModel.clearSelection()
      }
      updatePayEnabled()
    })

    // Enable pay button only when a provider is chosen for the selected method
    bankCombo.valueProperty.addListener((_, _, _) => updatePayEnabled())
    walletCombo.valueProperty.addListener((_, _, _) => updatePayEnabled())

    amountLabel.setText(f"Total to pay: RM${CartService.total}%.2f")
    updatePayEnabled()
  }

  // Show or hide the bank selection dropdown with layout management
  private def setBankVisible(v: Boolean): Unit = {
    bankCombo.setVisible(v)
    bankCombo.setManaged(v)   // collapse space when hidden
  }

  // Show or hide the e-wallet selection dropdown with layout management
  private def setWalletVisible(v: Boolean): Unit = {
    walletCombo.setVisible(v)
    walletCombo.setManaged(v) // collapse space when hidden
  }

  private def updatePayEnabled(): Unit = {
    val methodChosen =
      (rbOnline.isSelected && bankCombo.getValue != null) ||
        (rbWallet.isSelected && walletCombo.getValue != null)
    val enable = methodChosen && !CartService.isEmpty
    payBtn.setDisable(!enable)
  }

  // Return to donate food page
  @FXML private def goBack(): Unit =
    Router.showCenter("/san/view/DonateFood.fxml")

  // Process payment and record donation
  @FXML private def payNow(): Unit = {
    if (CartService.isEmpty) {
      val err = new Alert(AlertType.ERROR)
      err.setTitle("Payment")
      err.setHeaderText("Cart is empty")
      err.setContentText("Please add items before paying.")
      err.showAndWait()
      return
    }

    val method: PaymentMethod =
      if (rbOnline.isSelected) OnlineBanking(Option(bankCombo.getValue).getOrElse("Unknown"))
      else                     EWallet(Option(walletCombo.getValue).getOrElse("Unknown"))

    // Create donation record
    val donor = Session.currentUser.getOrElse(new san.model.Donor("guest"))
    val donation = Donation(
      id        = UUID.randomUUID().toString,
      amount    = CartService.total,
      method    = method,
      donor     = donor,
      timestamp = LocalDateTime.now()
    )

    // Save donation into database
    DonationRepository.insert(donation)

    // Show payment success message
    val ok = new Alert(AlertType.INFORMATION)
    ok.setTitle("Payment")
    ok.setHeaderText("Payment simulated")
    ok.setContentText(s"Method: ${method.label}\nAmount: " + f"RM${donation.amount}%.2f" + s"\nThank you, ${donor.username}!")
    ok.showAndWait()

    CartService.clear() // Empty the cart
    // Navigate to history page to show donation history
    Router.showCenter("/san/view/History.fxml")
  }
}
