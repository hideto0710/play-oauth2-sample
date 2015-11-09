package com.hideto0710.oauth2sample.api.controllers

import java.security.SecureRandom
import javax.inject.Inject

import com.hideto0710.oauth2sample.api.models.{OAuthAuthorizationCode, OAuthAuthorizationCodeDAO}
import com.hideto0710.oauth2sample.api.services._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class OAuthAuthorizationCodeController @Inject()(oAuthAuthorizationCodeDAO: OAuthAuthorizationCodeDAO) extends Controller {

  def insertOAuthAuthorizationCode() = Action.async(parse.json) { implicit request =>
    request.body.validate[OAuthAuthorizationCodeRequest].map { oacr =>
      val now = DateTime.now()
      val code = new Random(new SecureRandom()).alphanumeric.take(30).mkString
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
    }.recoverTotal {
      e => Future(BadRequest)
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
