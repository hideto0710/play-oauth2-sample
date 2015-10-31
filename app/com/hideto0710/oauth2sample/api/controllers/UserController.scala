package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import javax.inject.Inject

import com.hideto0710.oauth2sample.api.models.{User, UserDAO}
import com.hideto0710.oauth2sample.api.services.{UserRequest, UsersResponse, UserResponse}

class UserController @Inject()(userDAO: UserDAO) extends Controller {

  //import org.slf4j.LoggerFactory
  //private val logger = LoggerFactory.getLogger("com.hideto0710.oauth2sample.api.controllers.UserController")

  def insertUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[UserRequest].map {
      case ur: UserRequest =>
        userDAO.insert(User(None, ur.name)).map(_ => Ok)
    }.recoverTotal {
      e => Future(BadRequest)
    }
  }

  def getUsers = Action.async { implicit request =>
    userDAO.all().map {
      case u if u.nonEmpty =>
        Ok(Json.toJson(UsersResponse(u.length, u)))
      case _ =>
        Ok(Json.toJson(UsersResponse(0, Seq.empty)))
    }
  }

  def getUser(id: Long) = Action.async { implicit request =>
    userDAO.select(id).map {
      case us if us.nonEmpty =>
        val u = us.head
        Ok(Json.toJson(UserResponse(u.id.get, u.name)))
      case _ =>
        NotFound
    }
  }
}
