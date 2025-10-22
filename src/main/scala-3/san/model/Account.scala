package san.model

// Abstract class to demonstrate inheritance
abstract class Account(val username: String)

// Concrete implementation representing a food donor user
final class Donor(u: String) extends Account(u)
