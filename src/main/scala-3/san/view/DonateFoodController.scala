package san.view

import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout.{VBox, FlowPane}
import san.model.{CartItem, Food}
import san.service.{CartService, Router}
import javafx.scene.Node
import javafx.scene.input.MouseButton
import javafx.scene.control.Tooltip
import scala.jdk.CollectionConverters.*

class DonateFoodController {

  @FXML private var cartTable: TableView[CartItem] = _ // Table showing items in cart
  @FXML private var foodColumn: TableColumn[CartItem, String] = _ // Column showing food names
  @FXML private var quantityColumn: TableColumn[CartItem, Int] = _ // Column showing quantities
  @FXML private var priceColumn: TableColumn[CartItem, java.lang.Double] = _ // Column showing calculated prices
  @FXML private var totalLabel: Label = _ // Label displaying cart total
  @FXML private var foodGrid: FlowPane = _ // Grid container for food cards
  @FXML private var proceedBtn: Button = _ // Button to proceed for food cards
  @FXML private var searchField: TextField = _ // Search input field

  // Master list of available foods with their details
  private val availableFoods = List(
    Food("Apple",  2.00, "/images/apple.jpg", "A crunchy and sweet fruit.", "Calories: 95, Protein: 0.5g, Fiber: 4.4g"),
    Food("Orange", 4.00, "/images/orange.jpg", "A citrus fruit high in Vitamin C.", "Calories: 62, Protein: 1.2g, Fiber: 3.1g"),
    Food("Bread",  3.50, "/images/bread.png", "A loaf of freshly baked bread.", "Calories: 80, Protein: 2.5g, Fiber: 1.1g"),
    Food("Milk",   4.00, "/images/milk.jpg", "A nutritious drink rich in calcium.", "Calories: 122, Protein: 8g, Fiber: 0g"),
    Food("Carrot", 2.00, "/images/carrot.jpg", "A crunchy vegetable rich in beta-carotene.", "Calories: 41, Protein: 0.9g, Fiber: 3.0g"),
    Food("Potato", 1.50, "/images/potato.jpg", "A starchy tuber high in potassium.", "Calories: 77, Protein: 2g, Fiber: 2.2g"),
    Food("Noodle", 1.00, "/images/noodle.jpg", "A quick and convenient meal option.", "Calories: 380, Protein: 8g, Fiber: 2g"),
    Food("Rice", 1.00, "/images/rice.jpg", "A fundamental cereal grain which is rich in carbohydrates", "Calories: 205, Protein: 4.3g, Fiber: 0.6g")
  )

  @FXML
  private def initialize(): Unit = {
    // Set up how each column gets its data from CartItem objects
    foodColumn.setCellValueFactory(new PropertyValueFactory[CartItem, String]("food"))
    quantityColumn.setCellValueFactory(new PropertyValueFactory[CartItem, Int]("quantity"))
    // Price column shows calculated total
    priceColumn.setCellValueFactory(cd =>
      new ReadOnlyObjectWrapper[java.lang.Double](cd.getValue.getQuantity * cd.getValue.getPrice)
    )
    cartTable.setItems(CartService.items)

    // Disable Proceed To Payment button when cart is empty
    proceedBtn.disableProperty().bind(Bindings.isEmpty(CartService.items))

    if (searchField != null) {
      searchField.textProperty().addListener((_, _, q) => renderFoods(filterFoods(q)))
      searchField.setOnKeyPressed(ev => if (ev.getCode == KeyCode.ESCAPE) searchField.clear())
    }

    renderFoods(availableFoods) // show all foods initially
    updateTotal() // Calculate and display initial total
  }

  // Search food entered by user
  private def filterFoods(q: String): Seq[Food] = {
    val s = Option(q).getOrElse("").trim.toLowerCase
    if (s.isEmpty) availableFoods
    else availableFoods.filter(f => f.name.toLowerCase.contains(s))
  }

  // Make a UI node clickable to show food details
  private def attachDetails(node: Node, food: Food): Unit = {
    node.getStyleClass.add("clickable")
    // Show details popup when clicked
    node.setOnMouseClicked(ev => if (ev.getButton == MouseButton.PRIMARY) showFoodDetails(food))
  }

  // Display the food cards grid from a filtered list
  private def renderFoods(list: Seq[Food]): Unit = {
    foodGrid.getChildren.clear()

    // Show message if no foods match the search
    if (list.isEmpty) {
      val empty = new Label("No foods match your search.")
      empty.getStyleClass.add("h3")
      foodGrid.getChildren.add(empty)
      return
    }

    list.foreach { food =>
      val card = new VBox(5)
      card.setAlignment(Pos.CENTER)
      card.getStyleClass.add("food-card")
      card.setPrefWidth(150)

      val img = new ImageView(new Image(getClass.getResourceAsStream(food.imagePath)))
      img.setFitWidth(80); img.setFitHeight(80)

      val nameLabel = new Label(food.name); nameLabel.getStyleClass.add("h3")
      val priceLabel = new Label(f"RM${food.price}%.2f"); priceLabel.getStyleClass.add("price-green")
      priceLabel.setStyle("-fx-text-fill:#2e7d32;")

      // Add to cart button
      val addButton = new Button("Add to Cart")
      addButton.setOnAction(_ => {
        CartService.add(food.name, food.price, 1)
        cartTable.refresh()
        updateTotal()
      })

      // Make image and name clickable for details popup
      attachDetails(img, food)
      attachDetails(nameLabel, food)
      Tooltip.install(img, new Tooltip("View details"))
      Tooltip.install(nameLabel, new Tooltip("View details"))

      card.getChildren.addAll(img, nameLabel, priceLabel, addButton)
      foodGrid.getChildren.add(card)
    }
  }

  private def showFoodDetails(food: Food): Unit = {
    // Create a simple alert dialog to show food details
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle(s"Details for ${food.name}")
    alert.setHeaderText(null)
    alert.setContentText(
      s"Food: ${food.name}\nPrice: RM${food.price.formatted("%.2f")}\n\n" +
        s"Description: ${food.description}\n" +
        s"Nutritional Info: ${food.nutrition}\n\n" +
        s"Click 'OK' to continue."
    )
    alert.showAndWait() // Show dialog and wait user to close it
  }

  // Update the total amount label with current cart total
  private def updateTotal(): Unit = {
    val total = CartService.total
    totalLabel.setText(f"RM$total%.2f")
  }

  // Removes selected item from cart
  @FXML
  private def handleDelete(): Unit = {
    val selected = cartTable.getSelectionModel.getSelectedItem
    if (selected != null) {
      CartService.remove(selected.getFood) // Remove from cart service
      cartTable.getSelectionModel.clearSelection() // Clear table selection
      cartTable.refresh() // Update table display
      updateTotal() // Recalculate total
    }
  }

  // Allow user to change quantity of selected food
  @FXML
  private def handleEdit(): Unit = {
    val selected = cartTable.getSelectionModel.getSelectedItem
    if (selected == null) return

    val dialog = new TextInputDialog(selected.getQuantity.toString)
    dialog.setTitle("Edit Quantity")
    dialog.setHeaderText(s"Edit quantity for ${selected.getFood}")
    dialog.setContentText("Enter a whole number (1–99):")

    val result = dialog.showAndWait()
    result.ifPresent { str =>
      val qtyOpt = try Some(str.trim.toInt) catch { case _: NumberFormatException => None }
      qtyOpt match {
        case Some(q) if q >= 1 && q <= 99 =>
          CartService.setQuantity(selected.getFood, q)
          cartTable.refresh(); updateTotal()
        case _ =>
          // Invalid input - show error dialog
          val alert = new Alert(AlertType.ERROR)
          alert.setTitle("Invalid quantity")
          alert.setHeaderText(null)
          alert.setContentText("Please enter a whole number between 1 and 99.")
          alert.showAndWait()
      }
    }
  }

  // Navigate to payment selection
  // Validate that the cart is not empty before proceeding
  @FXML
  private def handleProceed(): Unit = {
    if (CartService.isEmpty) {
      val a = new Alert(AlertType.WARNING)
      a.setTitle("Cart is empty")
      a.setHeaderText(null)
      a.setContentText("Please add at least one item before proceeding to payment.")
      a.showAndWait()
      return
    }
    // Navigate to payment selection screen
    Router.showCenter("/san/view/PaymentSelection.fxml")
  }
}
