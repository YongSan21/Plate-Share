package san.db

import org.mindrot.jbcrypt.BCrypt
import scalikejdbc.*
import san.model.Donor

object UserRepository {

  def initializeTable(): Unit =
    DB autoCommit { implicit s =>
      sql"""
        CREATE TABLE users (
          user_id       varchar(64) PRIMARY KEY,
          password_hash varchar(100) NOT NULL,
          full_name     varchar(128)
        )
      """.execute.apply()
    }

  // Seed two demo users on first run
  def seedIfEmpty(): Unit =
    DB readOnly { implicit s =>
      val c = sql"select count(*) as c from users".map(_.int("c")).single.apply().getOrElse(0)
      if (c == 0) {
        create("san", "123456", "San")
        create("tim", "111111", "Tim")
        create("lim", "222222", "Lim")
      }
    }

  def create(userId: String, passwordPlain: String, fullName: String): Unit =
    DB autoCommit { implicit s =>
      val hash = BCrypt.hashpw(passwordPlain, BCrypt.gensalt()) // Hash the password before saving
      sql"insert into users(user_id,password_hash,full_name) values ($userId,$hash,$fullName)".update.apply()
    }

  // Validate user login (by checking password hash)
  def validate(userId: String, passwordPlain: String): Boolean =
    DB readOnly { implicit s =>
      sql"select password_hash from users where user_id=$userId"
        .map(_.string("password_hash")).single.apply()
        .exists(stored => BCrypt.checkpw(passwordPlain, stored))  // Use BCrypt to compare the hashed password
    }

  // Find user and return their password hash
  def find(userId: String): Option[String] =
    DB readOnly { implicit s =>
      sql"select password_hash from users where user_id=$userId"
        .map(_.string("password_hash")).single.apply()
    }

}
