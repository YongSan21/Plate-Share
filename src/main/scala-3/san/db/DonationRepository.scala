package san.db

import scalikejdbc.*
import san.model.{Donation, Donor, PaymentMethod}
import java.time.LocalDateTime

object DonationRepository {

  def initializeTable(): Unit =
    DB autoCommit { implicit s =>
      sql"""
        CREATE TABLE donations (
          id         int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          user_id    varchar(64) NOT NULL,
          amount     double      NOT NULL,
          method     varchar(64) NOT NULL,
          created_at varchar(64) NOT NULL
        )
      """.execute.apply()
    }

  def insert(d: Donation): Unit =
    DB autoCommit { implicit s =>
      sql"""
        insert into donations(user_id, amount, method, created_at)
        values (${d.donor.username}, ${d.amount}, ${d.method.label}, ${d.timestamp.toString})
      """.update.apply()
    }

  def findByUser(userId: String): List[Donation] =
    DB readOnly { implicit s =>
      sql"""
        select id, amount, method, created_at
        from donations
        where user_id = $userId
        order by created_at desc
      """.map { rs =>
        val id = rs.long("id").toString
        val amt = rs.double("amount")
        val pm = PaymentMethod.fromLabel(rs.string("method"))
        val ts = java.time.LocalDateTime.parse(rs.string("created_at"))

        Donation(id, amt, pm, Donor(userId), ts)
      }.list.apply()
    }
}
