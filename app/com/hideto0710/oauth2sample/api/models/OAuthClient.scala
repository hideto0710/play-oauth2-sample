package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import org.joda.time.DateTime
import javax.inject.Inject

case class OAuthClient(
  id: Option[Long],
  ownerId: Long,
  grantType: String,
  clientId: String,
  clientSecret: String,
  redirectUri: Option[String],
  createdAt: DateTime
)

class OAuthClientDAO @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  userDAO: UserDAO
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import com.github.tototoshi.slick.H2JodaSupport._

  private class OauthClientTable(tag: Tag) extends Table[OAuthClient](tag, "oauth_client") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def ownerId = column[Long]("owner_id")
    def grantType = column[String]("grant_type")
    def clientId = column[String]("client_id")
    def clientSecret = column[String]("client_secret")
    def redirectUri = column[String]("redirect_uri")
    def createdAt = column[DateTime]("created_at")

    def * = (id.?, ownerId, grantType, clientId, clientSecret, redirectUri.?, createdAt) <> (OAuthClient.tupled, OAuthClient.unapply)
  }

  private val oauthClientTable = TableQuery[OauthClientTable]
  private val users = TableQuery[userDAO.UserTable]

  def insert(oc: OAuthClient): Future[Long] = {
    val queryWithId = oauthClientTable returning oauthClientTable.map(_.id) += oc
    db.run(queryWithId).map(r => r)
  }

  def all(): Future[Option[Seq[OAuthClient]]] = {
    db.run(oauthClientTable.result).map(oc =>
      if (oc.nonEmpty) Some(oc) else None
    )
  }

  def select(id: Long): Future[Option[OAuthClient]] = {
    db.run(oauthClientTable.filter(_.id === id).result).map(_.headOption)
  }

  def delete(id: Long): Future[Option[Int]] = {
    db.run(oauthClientTable.filter(_.id === id).delete).map(i =>
      if (i == 0) None else Some(i)
    )
  }

  def validate(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    db.run(oauthClientTable
      .filter(_.clientId === clientId)
      .filter(_.clientSecret === clientSecret)
      .filter(_.grantType === grantType)
      .result
    ).map(_ != 0)
  }

  def findByClientId(clientId: String): Future[Option[OAuthClient]] = {
    db.run(oauthClientTable
      .filter(_.clientId === clientId)
      .result
    ).map(_.headOption)
  }

  def findClientCredentials(clientId: String, clientSecret: String): Future[Option[User]] = {
    db.run((for {
      oc <- oauthClientTable
        .filter(_.clientId === clientId)
        .filter(_.clientSecret === clientSecret)
        .filter(_.grantType === "client_credentials")
      u <- users if oc.ownerId === u.id
    } yield u).result).map(_.headOption)
  }
}