package com.hideto0710.oauth2sample.api.controllers

import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(com.hideto0710.oauth2sample.views.html.index("Your new application is ready."))
  }

}
