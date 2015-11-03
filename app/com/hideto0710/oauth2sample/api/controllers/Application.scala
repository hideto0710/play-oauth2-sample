package com.hideto0710.oauth2sample.api.controllers

import play.api.libs.json.{Writes, Json}
import play.api.mvc.{Action, Controller}
import scalaoauth2.provider.AuthInfo
import scalaoauth2.provider.OAuth2ProviderActionBuilders._
import javax.inject.Inject

import com.hideto0710.oauth2sample.api.models._

class Application @Inject()(
  accountDAO: AccountDAO,
  oauthClientDAO: OAuthClientDAO,
  oAuthAccessToken: OAuthAccessTokenDAO,
  oAuthAuthorizationCodeDAO: OAuthAuthorizationCodeDAO
) extends Controller {

  implicit val authInfoWrites = new Writes[AuthInfo[Account]] {
    def writes(authInfo: AuthInfo[Account]) = {
      Json.obj(
        "account" -> Json.obj(
          "id" -> authInfo.user.id,
          "email" -> authInfo.user.email,
          "name" -> authInfo.user.name,
          "created_at" -> authInfo.user.createdAt
        ),
        "scope" -> authInfo.scope,
        "client_id" -> authInfo.clientId,
        "redirect_uri" -> authInfo.redirectUri
      )
    }
  }

  def index = Action {
    Ok(com.hideto0710.oauth2sample.views.html.index("Your new application is ready."))
  }

  def auth = AuthorizedAction(new MyDataHandler(accountDAO, oauthClientDAO, oAuthAccessToken, oAuthAuthorizationCodeDAO)) { implicit request =>
    Ok(Json.toJson(request.authInfo))
  }

}
