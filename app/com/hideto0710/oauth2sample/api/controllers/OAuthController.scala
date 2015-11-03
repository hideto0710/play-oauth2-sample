package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Controller, Action}
import play.api.db.slick.DatabaseConfigProvider

import scalaoauth2.provider._
import scalaoauth2.provider.{DataHandler, ClientCredential, AccessToken, AuthInfo}

import javax.inject.Inject

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
  oauthClientDAO: OAuthClientDAO,
  oAuthAccessToken: OAuthAccessTokenDAO
) extends DataHandler[Account] {

  def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =
    oauthClientDAO.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType)

  def findUser(username: String, password: String): Future[Option[Account]] =
    accountDAO.authenticate(username, password)

  def createAccessToken(authInfo: AuthInfo[Account]): Future[AccessToken] = ???

  def getStoredAccessToken(authInfo: AuthInfo[Account]): Future[Option[AccessToken]] =
    oAuthAccessToken.findByAuthorized(authInfo.user, authInfo.clientId.getOrElse(""))

  def refreshAccessToken(authInfo: AuthInfo[Account], refreshToken: String): Future[AccessToken] = ???

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[Account]]] = ???

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[Account]]] = ???

  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[Account]] =
    oauthClientDAO.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""))

  def deleteAuthCode(code: String): Future[Unit] = ???

  def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[Account]]] = ???

}