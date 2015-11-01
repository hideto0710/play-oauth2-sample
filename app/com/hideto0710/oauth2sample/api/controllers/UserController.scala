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
    request.body.validate[UserRequest].map { ur =>
      userDAO.insert(User(None, ur.name)).map(r =>
        Ok(Json.toJson(UserResponse(r, ur.name)))
      )
    }.recoverTotal {
      e => Future(BadRequest)
    }
  }

  def getUsers = Action.async { implicit request =>
    userDAO.all().map {
      case Some(us) => Ok(Json.toJson(UsersResponse(us.length, us)))
      case None =>  Ok(Json.toJson(UsersResponse(0, Seq.empty)))
    }
  }

  def getUser(id: Long) = Action.async { implicit request =>
    userDAO.select(id).map {
      case Some(u) => Ok(Json.toJson(UserResponse(u.id.get, u.name)))
      case None => NotFound
    }
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    userDAO.delete(id).map {
      case Some(x) => Ok
      case None => NotFound
    }
  }
}
