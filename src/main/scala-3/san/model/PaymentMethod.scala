package san.model

// Sealed trait defining the contract for payment methods
sealed trait PaymentMethod { def label: String }
final case class OnlineBanking(bank: String) extends PaymentMethod {
  val label: String = s"Online Banking ($bank)"
}
final case class EWallet(provider: String) extends PaymentMethod {
  val label: String = s"eWallet ($provider)"
}

// Companion object providing utility methods for PaymentMethod
object PaymentMethod {
  def fromLabel(lbl: String): PaymentMethod = {
    val l = lbl.toLowerCase
    val inside = {
      val i = lbl.indexOf('('); val j = lbl.indexOf(')')
      if (i >= 0 && j > i) Some(lbl.substring(i + 1, j)) else None
    }
    if (l.startsWith("online")) OnlineBanking(inside.getOrElse("Bank"))
    else if (l.startsWith("ewallet")) EWallet(inside.getOrElse("Wallet"))
    else EWallet(lbl)
  }
}
