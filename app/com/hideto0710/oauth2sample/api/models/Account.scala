package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import java.security.MessageDigest
import org.joda.time.DateTime
import javax.inject.Inject

case class Account(id: Option[Long], name: String, email: String, password: String, createdAt: DateTime)

class AccountDAO @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import com.github.tototoshi.slick.H2JodaSupport._

  class AccountTable(tag: Tag) extends Table[Account](tag, "account") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def password = column[String]("password")
    def createdAt = column[DateTime]("created_at")

    def * = (id.?, name, email, password, createdAt) <> (Account.tupled, Account.unapply)
  }

  private val accounts = TableQuery[AccountTable]

  private def digestString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(s.getBytes)
    md.digest.foldLeft("") { (s, b) =>
      s + "%02x".format(if (b < 0) b + 256 else b)
    }
  }

  def insert(ac: Account): Future[Long] = {
    val queryWithId = (accounts returning accounts.map(_.id)) += ac.copy(password = digestString(ac.password))
    db.run(queryWithId).map(r => r)
  }

  def all(): Future[Option[Seq[Account]]] = {
    db.run(accounts.result).map(as =>
      if (as.nonEmpty) Some(as) else None
    )
  }

  def select(id: Long): Future[Option[Account]] = {
    db.run(accounts.filter(_.id === id).result).map(r =>
      r.headOption
    )
  }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(accounts.filter(_.id === id).delete).map(i =>
      if (i == 0) None else Some(i)
    )
  }

  def authenticate(email: String, password: String): Future[Option[Account]] = {
    db.run(accounts.filter(_.email === email).filter(_.password === password).result).map(r =>
      r.headOption
    )
  }
}