package san.service

import org.mindrot.jbcrypt.BCrypt
import san.model.User
import san.db.UserRepository

class Authentication {
  // Validate user login credentials against stored data
  def validateLogin(user: User): Either[String, String] = {
    // Check for empty or invalid input before database operations
    if (user.userId.value.isEmpty) Left("User ID cannot be empty")
    else if (user.password.value.length < 6) Left("Password must be at least 6 characters")
    else {
      // Retrieve stored password hash for the given username
      val storedPasswordHash = UserRepository.find(user.userId.value) match {
        case Some(hash) => hash
        case None => return Left("Invalid username or password")  // User doesn't exist
      }

      // Compare entered password with stored password hash using BCrypt
      if (BCrypt.checkpw(user.password.value, storedPasswordHash)) {
        // Password matches: successful authentication
        Right(s"Welcome, ${user.userId.value}!")
      } else {
        // Password does not match: authentication failed
        Left("Invalid username or password")
      }
    }
  }
}
