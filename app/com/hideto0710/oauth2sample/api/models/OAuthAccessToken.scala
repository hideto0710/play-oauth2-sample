package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import org.joda.time.DateTime
import javax.inject.Inject

import scalaoauth2.provider.AccessToken

case class OAuthAccessToken(
  id: Option[Long],
  accountId: Long,
  oauthClientId: Long,
  accessToken: String,
  refreshToken: String,
  createdAt: DateTime
)

class OAuthAccessTokenDAO @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  accountDAO: AccountDAO,
  oAuthClientDAO: OAuthClientDAO
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import com.github.tototoshi.slick.H2JodaSupport._

  private class OAuthAccessTokenTable(tag: Tag) extends Table[OAuthAccessToken](tag, "oauth_access_token") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accountId = column[Long]("account_id")
    def oauthClientId = column[Long]("oauth_client_id")
    def accessToken = column[String]("access_token")
    def refreshToken = column[String]("refresh_token")
    def createdAt = column[DateTime]("created_at")

    def * = (id.?, accountId, oauthClientId, accessToken, refreshToken, createdAt) <> (OAuthAccessToken.tupled, OAuthAccessToken.unapply)
  }

  private val oAuthAccessTokens = TableQuery[OAuthAccessTokenTable]
  private val oAuthClients = TableQuery[oAuthClientDAO.OAuthClientTable]
  private val accounts = TableQuery[accountDAO.AccountTable]

  private val accessTokenExpireSeconds = 3600

  private def toAccessToken(accessToken: OAuthAccessToken) = AccessToken(
    accessToken.accessToken,
    Some(accessToken.refreshToken),
    None,
    Some(accessTokenExpireSeconds),
    accessToken.createdAt.toDate
  )

  def findByAccessToken(accessToken: String): Future[Option[OAuthAccessToken]] = {
    db.run(oAuthAccessTokens
      .filter(_.accessToken === accessToken)
      .result
    ).map(_.headOption)
  }

  def findByAuthorized(account: Account, clientId: String): Future[Option[AccessToken]] = {
    db.run((for {
      oc <- oAuthClients
        .filter(_.clientId === clientId)
      oat <- oAuthAccessTokens
        .filter(_.accountId === account.id)
    } yield oat).result).map(_.map(toAccessToken).headOption)
  }
}