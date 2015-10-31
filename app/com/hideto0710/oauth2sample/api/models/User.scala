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

  def insert(user: User): Future[Unit] = db.run(Users += user).map { _ => () }

  def all(): Future[Seq[User]] = db.run(Users.result)

  def select(id: Long): Future[Seq[User]] = {
    db.run((for { u <- Users if u.id === id } yield u).result)
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "User") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (id.?, name) <> (User.tupled, User.unapply)
  }
}