package com.hideto0710.oauth2sample.api.services

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat

import com.hideto0710.oauth2sample.api.models.Account

case class AccountResponse(id: Long, name: String, email: String, createdAt: String)

object AccountResponse {
  def dateTimeToString(dt: DateTime) = {
    dt.toString(ISODateTimeFormat.dateTimeNoMillis())
  }

  implicit val accountResponseWrites = new Writes[AccountResponse] {
    override def writes(ur: AccountResponse) = Json.obj(
      "id" -> ur.id,
      "name" -> ur.name,
      "email" -> ur.email,
      "created_at" -> ur.createdAt
    )
  }
}

case class AccountRequest(name: String, email: String, password: String)

object AccountRequest {

  implicit val accountRequestReads: Reads[AccountRequest] = (
    (__ \ "name").read[String] and
    (__ \ "email").read[String] and
    (__ \ "password").read[String])(AccountRequest.apply _)

}

case class AccountsResponse(total: Int, accounts: Seq[Account])

object AccountsResponse {
  implicit val accountWrites = new Writes[Account] {
    override def writes(u: Account) = Json.obj(
      "id" -> u.id,
      "name" -> u.name,
      "email" -> u.email,
      "created_at" -> AccountResponse.dateTimeToString(u.createdAt)
    )
  }

  implicit val accountsResponseWrites = new Writes[AccountsResponse] {
    override def writes(ur: AccountsResponse) = Json.obj(
      "total" -> ur.total,
      "accounts" -> ur.accounts.map(accountWrites.writes)
    )
  }
}