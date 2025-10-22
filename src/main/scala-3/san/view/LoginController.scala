package san.view

import javafx.fxml.FXML
import javafx.scene.control.{Alert, PasswordField, TextField}
import javafx.scene.control.Alert.AlertType
import san.model.{Donor, User}
import san.service.{Authentication, Router, Session}
import san.db.UserRepository

class LoginController {

  @FXML private var userIdField: TextField = _ // Input field for userID
  @FXML private var passwordField: PasswordField = _ // Input field for password

  // Authentication service for validating login credentials
  private val authentication = new Authentication()

  // Authenticate user and start session
  @FXML
  def handleLogin(): Unit = {
    val userId = userIdField.getText.trim
    val password = passwordField.getText
    val user = new User(userId, password)

    // Attempt authentication using the authentication service
    authentication.validateLogin(user) match {
      case Left(error) =>
        val alert = new Alert(AlertType.ERROR)
        alert.setTitle("Login Error")
        alert.setHeaderText(null)
        alert.setContentText(error)
        alert.showAndWait()

      case Right(message) =>
        Session.setUser(Some(new san.model.Donor(userId)))  // Store user in session
        // Show success message when logged in
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("Login Successful")
        alert.setHeaderText(null)
        alert.setContentText(message)
        alert.showAndWait()
        // Navigate to home page
        Router.showCenter("/san/view/Home.fxml")
    }
  }

  // Create new user account
  // Validate input, creates account in database and redirects to login
  @FXML
  def handleSignUp(): Unit = {
    val userId = userIdField.getText.trim
    val password = passwordField.getText

    // Validate fields before attempting to create a new account
    if (userId.isEmpty || password.isEmpty) {
      val alert = new Alert(AlertType.ERROR)
      alert.setTitle("Sign Up Error")
      alert.setHeaderText(null)
      alert.setContentText("UserID and Password cannot be empty.")
      alert.showAndWait()
      return
    }

    // Password length check: Ensure the password is at least 6 characters long
    if (password.length < 6) {
      val alert = new Alert(AlertType.ERROR)
      alert.setTitle("Sign Up Error")
      alert.setHeaderText(null)
      alert.setContentText("Password must be at least 6 characters long.")
      alert.showAndWait()
      return
    }

    // Save the new user in the database
    UserRepository.create(userId, password, userId) // you can adjust fullName as needed

    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Sign Up Successful")
    alert.setHeaderText(null)
    alert.setContentText(s"Welcome, $userId! You can now log in.")
    alert.showAndWait()

    // Redirect user back to login page to allow user to sign in
    Router.showCenter("/san/view/Login.fxml")
  }

  // Return to login page
  @FXML
  def handleGoBack(): Unit = {
    Router.showCenter("/san/view/Login.fxml")
  }

  // Navigate to sign up page
  @FXML private def goToSignUp(): Unit = {
    Router.showCenter("/san/view/SignUp.fxml") // Navigate to Sign Up page
  }
}
