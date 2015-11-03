package com.hideto0710.oauth2sample.api.services

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

case class OAuthAuthorizationCodeResponse(
  id: Long,
  accountId: Long,
  oauthClientId: Long,
  code: String,
  redirectUri: String,
  createdAt: String
)

object OAuthAuthorizationCodeResponse {
  def dateTimeToString(dt: DateTime) = {
    dt.toString(ISODateTimeFormat.dateTimeNoMillis())
  }

  implicit val oAuthAuthorizationCodeResponse = new Writes[OAuthAuthorizationCodeResponse] {
    override def writes(oacr: OAuthAuthorizationCodeResponse) = Json.obj(
      "id" -> oacr.id,
      "account_id" -> oacr.accountId,
      "oauth_client_id" -> oacr.oauthClientId,
      "code" -> oacr.code,
      "redirect_uri" -> oacr.redirectUri,
      "created_at" -> oacr.createdAt
    )
  }
}

case class OAuthAuthorizationCodeRequest(
  accountId: Long,
  oauthClientId: Long,
  code: String,
  redirectUri: String
)

object OAuthAuthorizationCodeRequest {

  implicit val oAuthAuthorizationCodeRequest: Reads[OAuthAuthorizationCodeRequest] = (
    (__ \ "account_id").read[Long] and
    (__ \ "oauth_client_id").read[Long] and
    (__ \ "code").read[String] and
    (__ \ "redirect_uri").read[String])(OAuthAuthorizationCodeRequest.apply _)

}