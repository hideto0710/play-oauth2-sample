package com.hideto0710.oauth2sample.api.controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import org.joda.time.DateTime
import javax.inject.Inject
import scalaoauth2.provider.OAuth2ProviderActionBuilders._

import com.hideto0710.oauth2sample.api.models.{OAuthAccessTokenDAO, OAuthClientDAO, Account, AccountDAO}
import com.hideto0710.oauth2sample.api.services.{AccountRequest, AccountsResponse, AccountResponse}

class AccountController @Inject()(
  accountDAO: AccountDAO,
  oauthClientDAO: OAuthClientDAO,
  oAuthAccessToken: OAuthAccessTokenDAO
) extends Controller {

  def insertAccount() = Action.async(parse.json) { implicit request =>
    request.body.validate[AccountRequest].map { ar =>
      val now = DateTime.now()
      accountDAO.insert(Account(None, ar.name, ar.email, ar.password, now)).map(r =>
        Ok(Json.toJson(AccountResponse(r, ar.name, ar.email, AccountResponse.dateTimeToString(now))))
      )
    }.recoverTotal {
      e => Future(BadRequest)
    }
  }

  def getAccounts = Action.async { implicit request =>
    accountDAO.all().map {
      case Some(as) => Ok(Json.toJson(AccountsResponse(as.length, as)))
      case None =>  Ok(Json.toJson(AccountsResponse(0, Seq.empty)))
    }
  }

  def getAccount(id: Long) = AuthorizedAction(new MyDataHandler(accountDAO, oauthClientDAO, oAuthAccessToken)).async { implicit request =>
    print(request.authInfo)
    accountDAO.select(id).map {
      case Some(a) => Ok(Json.toJson(
        AccountResponse(a.id.get, a.name, a.email, AccountResponse.dateTimeToString(a.createdAt))
      ))
      case None => NotFound
    }
  }

  def deleteAccount(id: Long) = Action.async { implicit request =>
    accountDAO.delete(id).map {
      case Some(x) => Ok
      case None => NotFound
    }
  }
}
