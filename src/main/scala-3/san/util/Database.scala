package san.util

import scalikejdbc.*

trait Database:
  private val driver = "org.apache.derby.jdbc.EmbeddedDriver"
  private val url    = "jdbc:derby:./plateshareDB;create=true"  // creates files in project dir

  Class.forName(driver)
  ConnectionPool.singleton(url, "me", "mine")
  given AutoSession = AutoSession

object Database extends Database:
  def setupDB(): Unit =
    if (DB.getTable("USERS").isEmpty)     san.db.UserRepository.initializeTable()
    if (DB.getTable("DONATIONS").isEmpty) san.db.DonationRepository.initializeTable()
