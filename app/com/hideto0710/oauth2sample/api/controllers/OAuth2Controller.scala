package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.mvc.{Controller, Action}
import play.api.db.slick.DatabaseConfigProvider

import scalaoauth2.provider._
import scalaoauth2.provider.{DataHandler, ClientCredential, AccessToken, AuthInfo}

import javax.inject.Inject

import com.hideto0710.oauth2sample.api.models._

class OAuth2Controller @Inject()(
  dbConfigProvider: DatabaseConfigProvider,
  userDAO: UserDAO,
  oauthClientDAO: OAuthClientDAO
) extends Controller with OAuth2Provider {

  def accessToken = Action.async { implicit request =>
    issueAccessToken(new MyDataHandler(userDAO, oauthClientDAO))
  }
}

class MyDataHandler @Inject()(userDAO: UserDAO, oauthClientDAO: OAuthClientDAO) extends DataHandler[User] {

  def validateClient(clientCredential: ClientCredential, grantType: String): Future[Boolean] =
    oauthClientDAO.validate(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""), grantType)

  def findUser(username: String, password: String): Future[Option[User]] =
    userDAO.authenticate(username, password)

  def createAccessToken(authInfo: AuthInfo[User]): Future[AccessToken] = ???

  def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[AccessToken]] = ???

  def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[AccessToken] = ???

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = ???

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = ???

  def findClientUser(clientCredential: ClientCredential, scope: Option[String]): Future[Option[User]] =
    oauthClientDAO.findClientCredentials(clientCredential.clientId, clientCredential.clientSecret.getOrElse(""))

  def deleteAuthCode(code: String): Future[Unit] = ???

  def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[User]]] = ???

}