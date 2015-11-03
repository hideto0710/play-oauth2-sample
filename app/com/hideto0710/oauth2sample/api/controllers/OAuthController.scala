package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Controller, Action}
import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import scalaoauth2.provider._
import scalaoauth2.provider.{DataHandler, ClientCredential, AccessToken, AuthInfo}

import com.hideto0710.oauth2sample.api.models._

class OAuthController @Inject()(
  dbConfigProvider: DatabaseConfigProvider,
  accountDAO: AccountDAO,
  oauthClientDAO: OAuthClientDAO,
  oAuthAccessToken: OAuthAccessTokenDAO
) extends Controller with OAuth2Provider {

  def accessToken = Action.async { implicit request =>
    issueAccessToken(new MyDataHandler(accountDAO, oauthClientDAO, oAuthAccessToken))
  }
}

class MyDataHandler @Inject()(
  accountDAO: AccountDAO,
  oAuthClientDAO: OAuthClientDAO,
  oAuthAccessTokenDAO: OAuthAccessTokenDAO
) extends DataHandler[Account] {

  def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =
    oAuthClientDAO.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType)

  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[Account]] =
    oAuthClientDAO.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""))

  def getStoredAccessToken(authInfo: AuthInfo[Account]): Future[Option[AccessToken]] =
    oAuthAccessTokenDAO.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse(""))

  def createAccessToken(authInfo: AuthInfo[Account]): Future[AccessToken] = {
    (for {
      ci <- authInfo.clientId.toRight(new InvalidClient()).right
      optionOc <- Await.ready(oAuthClientDAO.findByClientId(ci), Duration.Inf)
        .value.get.toOption.toRight(new InvalidClient()).right
      oc <- optionOc.toRight(new InvalidClient()).right
    } yield oc) match {
      case Left(error) => throw error
      case Right(oc) =>
        oAuthAccessTokenDAO.insertWithReturnToken(oAuthAccessTokenDAO.create(authInfo, oc))
    }
  }

  def findUser(username: String, password: String): Future[Option[Account]] = accountDAO.authenticate(username, password)

  def refreshAccessToken(authInfo: AuthInfo[Account], refreshToken: String): Future[AccessToken] = ???

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[Account]]] = ???

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[Account]]] = ???

  def deleteAuthCode(code: String): Future[Unit] = ???

  def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[Account]]] = ???

}