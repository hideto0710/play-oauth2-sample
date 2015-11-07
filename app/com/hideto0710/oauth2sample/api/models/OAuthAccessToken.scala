package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import java.security.SecureRandom
import org.joda.time.DateTime
import javax.inject.Inject
import scalaoauth2.provider.AuthInfo

sealed trait OAuthAccessTokenTrait {
  val accessToken: String
  val refreshToken: String
  val createdAt: DateTime
}

case class OAuthAccessToken(
  id: Option[Long],
  accountId: Long,
  oauthClientId: Long,
  accessToken: String,
  refreshToken: String,
  createdAt: DateTime
) extends OAuthAccessTokenTrait

case class OAuthAccessTokenWithDetail(
  id: Option[Long],
  accountId: Long,
  account: Account,
  oauthClientId: Long,
  oauthClient: OAuthClient,
  accessToken: String,
  refreshToken: String,
  createdAt: DateTime
) extends OAuthAccessTokenTrait

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

  def insert(oat: OAuthAccessToken): Future[Long] = {
    db.run((oAuthAccessTokens returning oAuthAccessTokens.map(_.id)) += oat).map(r => r)
  }

  def insertWithReturnToken(oat: OAuthAccessToken): Future[OAuthAccessToken] = {
    insert(oat).map(r => oat.copy(id = Some(r)))
  }

  def create(authInfo: AuthInfo[Account], oAuthClient: OAuthClient) = {
    def randomString(length: Int) = new Random(new SecureRandom()).alphanumeric.take(length).mkString
    val accessToken = randomString(40)
    val refreshToken = randomString(40)
    val createdAt = DateTime.now()
    OAuthAccessToken(
      None,
      authInfo.user.id.get,
      oAuthClient.id.get,
      accessToken,
      refreshToken,
      createdAt
    )
  }

  def delete(account: Account, oAuthClient: OAuthClient): Future[Option[Int]] = {
    db.run(oAuthAccessTokens
      .filter(_.accountId === account.id)
      .filter(_.oauthClientId === oAuthClient.id)
      .delete
    ).map(i => if (i == 0) None else Some(i))
  }

  def refresh(authInfo: AuthInfo[Account], oAuthClient: OAuthClient): Future[OAuthAccessToken] = {
    for {
      i <- delete(authInfo.user, oAuthClient)
      oat <- insertWithReturnToken(create(authInfo, oAuthClient))
    } yield oat
  }

  def findByAccessToken(accessToken: String): Future[Option[OAuthAccessToken]] = {
    db.run(oAuthAccessTokens
      .filter(_.accessToken === accessToken)
      .result
    ).map(_.headOption)
  }


  private def getOAuthTokenDetail(oat: OAuthAccessToken, a: Account, oc: OAuthClient): OAuthAccessTokenWithDetail = {
    OAuthAccessTokenWithDetail(
      oat.id,
      oat.accountId,
      a,
      oat.oauthClientId,
      oc,
      oat.accessToken,
      oat.refreshToken,
      oat.createdAt
    )
  }

  def findByRefreshToken(refreshToken: String): Future[Option[OAuthAccessTokenWithDetail]] = {
    db.run((for {
      oat <- oAuthAccessTokens.filter(_.refreshToken === refreshToken)
      a <- accounts.filter(_.id === oat.accountId)
      oc <- oAuthClients.filter(_.id === oat.oauthClientId)
    } yield (oat, a, oc)).result).map(r =>
      r.headOption match {
        case Some(h) => Some(getOAuthTokenDetail(h._1, h._2, h._3))
        case None => None
      }
    )
  }

  def findDetailByAccessToken(accessToken: String): Future[Option[OAuthAccessTokenWithDetail]] = {
    db.run((for {
      oat <- oAuthAccessTokens.filter(_.accessToken === accessToken)
      as <- accounts.filter(_.id === oat.accountId)
      oc <- oAuthClients.filter(_.id === oat.oauthClientId)
    } yield (oat, as, oc)).result).map( r =>
      r.headOption match {
        case Some(h) => Some(getOAuthTokenDetail(h._1, h._2, h._3))
        case None => None
      }
    )
  }

  def findByAuthorized(account: Account, clientId: String): Future[Option[OAuthAccessToken]] = {
    db.run((for {
      oc <- oAuthClients
        .filter(_.clientId === clientId)
      oat <- oAuthAccessTokens
        .filter(_.accountId === account.id)
    } yield oat).result).map(_.headOption)
  }
}