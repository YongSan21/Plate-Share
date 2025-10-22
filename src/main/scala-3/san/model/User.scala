package san.model

import scalafx.beans.property.StringProperty

//Primary use cases: login form data binding, user authentication, form validation
class User(_userId: String, _password: String) {
  val userId: StringProperty = StringProperty(_userId)
  val password: StringProperty = StringProperty(_password)
}

