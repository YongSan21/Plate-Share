package san.service

import javafx.beans.property.{BooleanProperty, SimpleBooleanProperty}
import san.model.Donor

// Maintains the current user's login state
object Session {
  // Who is currently logged in
  var currentUser: Option[Donor] = None

  // Observable flag for UI binding
  val loggedIn: BooleanProperty = new SimpleBooleanProperty(false)

  // Update user state
  def setUser(user: Option[Donor]): Unit = {
    currentUser = user
    loggedIn.set(user.isDefined)

  }

  // Log out all users and clear session state
  def logoutAll(): Unit = {
    currentUser = None
    loggedIn.set(false)
  }
}
