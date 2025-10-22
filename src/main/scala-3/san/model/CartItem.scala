package san.model

class CartItem(var food: String, var quantity: Int, var price: Double) {
  // Get the food name for this cart item
  def getFood: String = food
  // Get the current quantity for this cart item
  def getQuantity: Int = quantity
  // Get the unit price for this cart item
  def getPrice: Double = price
  // Update the quantity of this cart item
  def setQuantity(q: Int): Unit = quantity = q
  // Update the unit price of this cart item
  def setPrice(p: Double): Unit = price = p
}
