package com.hideto0710.oauth2sample.api.services

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

import com.hideto0710.oauth2sample.api.models.User


case class UserResponse(id: Long, name: String, email: String)

object UserResponse {
  implicit val userResponse = new Writes[UserResponse] {
    override def writes(ur: UserResponse) = Json.obj(
      "id" -> ur.id,
      "name" -> ur.name,
      "email" -> ur.email
    )
  }
}


case class UserRequest(name: String, email: String, password: String)

object UserRequest {

  implicit val userRequest: Reads[UserRequest] = (
    (__ \ "name").read[String] and
    (__ \ "email").read[String] and
    (__ \ "password").read[String])(UserRequest.apply _)

}


case class UsersResponse(total: Int, users: Seq[User])

object UsersResponse {
  implicit val userWrites = new Writes[User] {
    override def writes(u: User) = Json.obj(
      "id" -> u.id,
      "name" -> u.name,
      "email" -> u.email
    )
  }

  implicit val UsersResponse = new Writes[UsersResponse] {
    override def writes(ur: UsersResponse) = Json.obj(
      "total" -> ur.total,
      "users" -> ur.users.map(userWrites.writes)
    )
  }
}