package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile

import javax.inject.Inject

case class User(id: Option[Long], name: String)

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  //import org.slf4j.LoggerFactory
  //private val logger = LoggerFactory.getLogger("com.hideto0710.oauth2sample.api.models.UserDAO")

  import driver.api._

  private val Users = TableQuery[UsersTable]

  def insert(user: User): Future[Long] = {
    val queryWithId = (Users returning Users.map(_.id)) += user
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

  private class UsersTable(tag: Tag) extends Table[User](tag, "User") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id.?, name) <> (User.tupled, User.unapply)
  }
}