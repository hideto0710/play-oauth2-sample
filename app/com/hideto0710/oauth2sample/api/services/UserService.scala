package com.hideto0710.oauth2sample.api.services

import com.hideto0710.oauth2sample.api.models.User
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper

case class UserResponse(id: Long, name: String)

object UserResponse {
  implicit val userResponse = new Writes[UserResponse] {
    override def writes(ur: UserResponse) = Json.obj(
      "id" -> ur.id,
      "name" -> ur.name
    )
  }
}


case class UserRequest(name: String)

object UserRequest {

  implicit val filterWordsUpdateRequest = new Reads[UserRequest] {
    override def reads(js: JsValue) =
      (js \ "name").validate[String].flatMap(
        words => new JsSuccess(UserRequest(words)))
  }

}


case class UsersResponse(total: Int, users: Seq[User])

object UsersResponse {
  implicit val userWrites = new Writes[User] {
    override def writes(u: User) = Json.obj(
      "id" -> u.id,
      "name" -> u.name
    )
  }

  implicit val UsersResponse = new Writes[UsersResponse] {
    override def writes(ur: UsersResponse) = Json.obj(
      "total" -> ur.total,
      "users" -> ur.users.map(userWrites.writes)
    )
  }
}