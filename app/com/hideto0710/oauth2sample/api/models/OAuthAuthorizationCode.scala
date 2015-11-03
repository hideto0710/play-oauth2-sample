package com.hideto0710.oauth2sample.api.models

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import org.joda.time.DateTime
import javax.inject.Inject

case class OAuthAuthorizationCode(
  id: Option[Long],
  accountId: Long,
  oauthClientId: Long,
  code: String,
  redirectUri: String,
  createdAt: DateTime
)

case class OAuthAuthorizationCodeWithDetail(
  id: Option[Long],
  accountId: Long,
  account: Account,
  oauthClientId: Long,
  oauthClient: OAuthClient,
  code: String,
  redirectUri: String,
  createdAt: DateTime
)

class OAuthAuthorizationCodeDAO @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  accountDAO: AccountDAO,
  oAuthClientDAO: OAuthClientDAO
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._
  import com.github.tototoshi.slick.H2JodaSupport._

  class OAuthAuthorizationCodeTable(tag: Tag) extends Table[OAuthAuthorizationCode](tag, "oauth_authorization_code") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accountId = column[Long]("account_id")
    def oauthClientId = column[Long]("oauth_client_id")
    def code = column[String]("code")
    def redirect_uri = column[String]("redirect_uri")
    def createdAt = column[DateTime]("created_at")

    def * = (id.?, accountId, oauthClientId, code, redirect_uri, createdAt) <> (OAuthAuthorizationCode.tupled, OAuthAuthorizationCode.unapply)
  }

  private val oAuthAuthorizationCodes = TableQuery[OAuthAuthorizationCodeTable]
  private val oAuthClients = TableQuery[oAuthClientDAO.OAuthClientTable]
  private val accounts = TableQuery[accountDAO.AccountTable]

  def insert(oac: OAuthAuthorizationCode): Future[Long] = {
    val queryWithId = oAuthAuthorizationCodes returning oAuthAuthorizationCodes.map(_.id) += oac
    db.run(queryWithId).map(r => r)
  }

  def select(id: Long): Future[Option[OAuthAuthorizationCode]] = {
    db.run(oAuthAuthorizationCodes.filter(_.id === id).result).map(_.headOption)
  }

  def delete(code: String): Future[Option[Int]] = {
    db.run(oAuthAuthorizationCodes.filter(_.code === code).delete).map(i =>
      if (i == 0) None else Some(i)
    )
  }

  def findByCode(code: String): Future[Option[OAuthAuthorizationCodeWithDetail]] = {
    for {
      result <- db.run((for {
        oac <- oAuthAuthorizationCodes
          .filter(_.code === code)
        as <- accounts
          .filter(_.id === oac.accountId)
        oc <- oAuthClients
          .filter(_.id === oac.oauthClientId)
      } yield (oac, as, oc)).result)
    } yield {
      result.headOption match {
        case Some(h) =>
          val oAuthAuthorizationCode = h._1
          Some(OAuthAuthorizationCodeWithDetail(
            oAuthAuthorizationCode.id,
            oAuthAuthorizationCode.accountId,
            h._2,
            oAuthAuthorizationCode.oauthClientId,
            h._3,
            oAuthAuthorizationCode.code,
            oAuthAuthorizationCode.redirectUri,
            oAuthAuthorizationCode.createdAt
          ))
        case None => None
      }
    }
  }
}