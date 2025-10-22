package san.model

import java.time.LocalDateTime

// Record of a completed donation transaction
// Captures all information about a donation
case class Donation(
                     id: String,
                     amount: Double,
                     method: PaymentMethod,
                     donor: Donor,
                     timestamp: LocalDateTime
                   )
