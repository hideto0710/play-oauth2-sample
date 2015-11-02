package com.hideto0710.oauth2sample.api.models

import java.security.MessageDigest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import javax.inject.Inject

case class User(id: Option[Long], name: String, email: String, password: String)

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  //import org.slf4j.LoggerFactory
  //private val logger = LoggerFactory.getLogger("com.hideto0710.oauth2sample.api.models.UserDAO")

  import driver.api._

  private val Users = TableQuery[UsersTable]

  private def digestString(s: String): String = {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(s.getBytes)
    md.digest.foldLeft("") { (s, b) =>
      s + "%02x".format(if (b < 0) b + 256 else b)
    }
  }

  def insert(user: User): Future[Long] = {
    val queryWithId = (Users returning Users.map(_.id)) += user.copy(password = digestString(user.password))
    db.run(queryWithId).map(r => r)
  }

  def all(): Future[Option[Seq[User]]] = {
    db.run(Users.result).map(us =>
      if (us.nonEmpty) Some(us) else None
    )
  }

  def select(id: Long): Future[Option[User]] = {
    db.run(Users.filter(_.id === id).result).map(r =>
      r.headOption
    )
  }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(Users.filter(_.id === id).delete).map(i =>
      if (i == 0) None else Some(i)
    )
  }

  def authenticate(email: String, password: String): Future[Option[User]] = {
    db.run(Users.filter(_.email === email).filter(_.password === password).result).map(r =>
      r.headOption
    )
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "User") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")
    def password = column[String]("password")

    def * = (id.?, name, email, password) <> (User.tupled, User.unapply)
  }
}