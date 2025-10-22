package san.service

import javafx.collections.{FXCollections, ObservableList}
import san.model.CartItem
import scala.jdk.CollectionConverters.*

final class Cart(private val repo: Repository[String, CartItem]) {
  // ObservableList automatically notifies bound UI components like TableView when items are added, removed or edited
  val items: ObservableList[CartItem] = FXCollections.observableArrayList[CartItem]()
  // Synchronize the observable list with repository data
  private def refresh(): Unit = items.setAll(repo.all.asJava)

  // Add food item to cart or increase quantity if already present
  // If item exists: increase quantity by specified amount
  // If item does not exist: create new cart item
  def add(food: String, unitPrice: Double, qty: Int = 1): Unit = {
    val updated = repo.get(food) match {
      case Some(it) => it.setQuantity(it.getQuantity + qty); it
      case None     => new CartItem(food, qty, unitPrice)
    }
    repo.upsert(food, updated); refresh()
  }

  // Set exact quantity for a cart item
  def setQuantity(food: String, qty: Int): Boolean = {
    repo.get(food) match {
      case Some(it) =>
        if (qty <= 0) repo.remove(food)
        else { it.setQuantity(qty); repo.upsert(food, it) }
        refresh(); true
      case None => false
    }
  }

  // Remove item completely from cart
  def remove(food: String): Unit = { repo.remove(food); refresh() }
  // Calculate total cost of all items in cart
  def total: Double = repo.all.map(i => i.getQuantity * i.getPrice).sum
  // Remove all items from cart
  // Used after successful payment
  def clear(): Unit = { repo.clear(); refresh() }
  // Check if cart contains any items
  def isEmpty: Boolean = repo.all.isEmpty
}

object CartService {
  private val cart = new Cart(new InMemoryRepository[String, CartItem]())
  // Get observable list of cart items for UI binding
  def items: ObservableList[CartItem] = cart.items
  // Add item to cart
  def add(food: String, unitPrice: Double, qty: Int = 1): Unit = cart.add(food, unitPrice, qty)
  // Set exact quantity for cart item
  def setQuantity(food: String, qty: Int): Boolean = cart.setQuantity(food, qty)
  // Remove item from cart
  def remove(food: String): Unit = cart.remove(food)
  // Get total cart value
  def total: Double = cart.total
  // Clear all cart items
  def clear(): Unit = cart.clear()
  // Check if cart is empty
  def isEmpty: Boolean = cart.isEmpty
}
