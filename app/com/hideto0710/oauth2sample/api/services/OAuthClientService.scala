package com.hideto0710.oauth2sample.api.services

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

case class OAuthClientResponse(
  id: Long,
  ownerId: Long,
  grantType: String,
  clientId: String,
  clientSecret: String,
  redirectUri: Option[String],
  createdAt: String
)

object OAuthClientResponse {
  def dateTimeToString(dt: DateTime) = {
    dt.toString(ISODateTimeFormat.dateTimeNoMillis())
  }

  implicit val oAuthClientResponse = new Writes[OAuthClientResponse] {
    override def writes(ocr: OAuthClientResponse) = Json.obj(
      "id" -> ocr.id,
      "owner_id" -> ocr.ownerId,
      "grant_type" -> ocr.grantType,
      "client_id" -> ocr.clientId,
      "client_secret" -> ocr.clientSecret,
      "redirect_uri" -> ocr.redirectUri,
      "created_at" -> ocr.createdAt
    )
  }
}


case class OAuthClientRequest(
  ownerId: Long,
  grantType: String,
  clientId: String,
  clientSecret: String,
  redirectUri: Option[String]
)

object OAuthClientRequest {

  implicit val oAuthClientRequest: Reads[OAuthClientRequest] = (
    (__ \ "owner_id").read[Long] and
    (__ \ "grant_type").read[String] and
    (__ \ "client_id").read[String] and
    (__ \ "client_secret").read[String] and
    (__ \ "redirect_uri").readNullable[String])(OAuthClientRequest.apply _)

}