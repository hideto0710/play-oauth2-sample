package com.hideto0710.oauth2sample.api.controllers

import java.security.SecureRandom
import javax.inject.Inject

import com.hideto0710.oauth2sample.api.models._
import com.hideto0710.oauth2sample.api.services._
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.util.Random
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

class OAuthAuthorizationCodeController @Inject()(
  accountDAO: AccountDAO,
  oauthClientDAO: OAuthClientDAO,
  oAuthAccessToken: OAuthAccessTokenDAO,
  oAuthAuthorizationCodeDAO: OAuthAuthorizationCodeDAO
) extends Controller {

  private val conf = ConfigFactory.load()
  private val CodeLength = conf.getInt("oauth.auth_code.length")

  def insertOAuthAuthorizationCode() =
    AuthorizedAction(new MyDataHandler(accountDAO, oauthClientDAO, oAuthAccessToken, oAuthAuthorizationCodeDAO)).async(parse.json)
    { implicit request =>
      (for {
        aId <- request.authInfo.user.id.toRight(Forbidden).right
        oacr <- request.body.validateOpt[OAuthAuthorizationCodeRequest].get.toRight(BadRequest).right
        _ <- (if (oacr.accountId == aId) Some(true) else None).toRight(Forbidden).right
      } yield oacr) match {
        case Left(error) => Future(error)
        case Right(oacr) =>
          val now = DateTime.now()
          val code = new Random(new SecureRandom()).alphanumeric.take(CodeLength).mkString
          oAuthAuthorizationCodeDAO.insert(
            OAuthAuthorizationCode(None, oacr.accountId, oacr.oauthClientId, code, oacr.redirectUri, now)
          ).map(r =>
            Ok(Json.toJson(
              OAuthAuthorizationCodeResponse(r,
                oacr.accountId,
                oacr.oauthClientId,
                code,
                oacr.redirectUri,
                AccountResponse.dateTimeToString(now)))
            )
          )
      }
    }

  def getOAuthAuthorizationCode(id: Long) = Action.async { implicit request =>
    oAuthAuthorizationCodeDAO.select(id).map {
      case Some(oac) => Ok(Json.toJson(
        OAuthAuthorizationCodeResponse(oac.id.get,
          oac.accountId,
          oac.oauthClientId,
          oac.code,
          oac.redirectUri,
          AccountResponse.dateTimeToString(oac.createdAt))
      ))
      case None => NotFound
    }
  }
}
