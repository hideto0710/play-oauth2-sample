package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import javax.inject.Inject
import org.joda.time.DateTime

import com.hideto0710.oauth2sample.api.models.{OAuthClient, OAuthClientDAO}
import com.hideto0710.oauth2sample.api.services._

class OAuthClientController @Inject()(oAuthClientDAO: OAuthClientDAO) extends Controller {

  def insertOAuthClient() = Action.async(parse.json) { implicit request =>
    request.body.validate[OAuthClientRequest].map { ocr =>
      val now = DateTime.now()
      oAuthClientDAO.insert(
        OAuthClient(
          None, ocr.ownerId, ocr.grantType, ocr.clientId, ocr.clientSecret, ocr.scope, ocr.redirectUri, now
        )
      ).map(r =>
        Ok(Json.toJson(
          OAuthClientResponse(r,
            ocr.ownerId,
            ocr.grantType,
            ocr.clientId,
            ocr.clientSecret,
            ocr.scope,
            ocr.redirectUri,
            AccountResponse.dateTimeToString(now)))
        )
      )
    }.recoverTotal {
      e => Future(BadRequest)
    }
  }

  def getOAuthClient(id: Long) = Action.async { implicit request =>
    oAuthClientDAO.select(id).map {
      case Some(oc) => Ok(Json.toJson(
        OAuthClientResponse(oc.id.get,
          oc.ownerId,
          oc.grantType,
          oc.clientId,
          oc.clientSecret,
          oc.scope,
          oc.redirectUri,
          AccountResponse.dateTimeToString(oc.createdAt))
      ))
      case None => NotFound
    }
  }

  def deleteOAuthClient(id: Long) = Action.async { implicit request =>
    oAuthClientDAO.delete(id).map {
      case Some(x) => Ok
      case None => NotFound
    }
  }
}
