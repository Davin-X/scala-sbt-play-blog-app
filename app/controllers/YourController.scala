// YourController.scala
package controllers

import actions.JwtAuthenticatedAction
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class YourController @Inject()(cc: ControllerComponents, jwtAuthenticatedAction: JwtAuthenticatedAction)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def protectedRoute: Action[AnyContent] = jwtAuthenticatedAction { implicit request =>
    // Your protected route logic here
    Ok(Json.obj("message" -> "Protected route accessed successfully"))
  }
}