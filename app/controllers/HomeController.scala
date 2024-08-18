package controllers

import actions.JwtAuthenticatedAction
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import utils.JwtUtil

import javax.inject.Inject
import scala.concurrent.Future

class HomeController @Inject()(cc: ControllerComponents, jwtAuthenticatedAction: JwtAuthenticatedAction) extends AbstractController(cc) {
  private val logger = Logger(getClass)
  def index: Action[AnyContent] = jwtAuthenticatedAction { implicit request: Request[AnyContent] =>
    Ok("This is a protected resource")
  }

  def login(username: String, password: String): Action[AnyContent] = Action.async { implicit request =>
    // Assuming password validation is done here
    val token = JwtUtil.createToken(username)
    logger.info(s"token : $token")
    Future.successful(Ok(Json.obj("token" -> token)))
  }

  def protectedResource: Action[AnyContent] = Action { implicit request =>
    request.headers.get("Authorization") match {
      case Some(authHeader) if authHeader.startsWith("Bearer ") =>
        val token = authHeader.substring(7)
        JwtUtil.validateToken(token) match {
          case Some(claim) =>
            JwtUtil.extractUsername(claim) match {
              case Some(username) => Ok(s"Hello, $username!")
              case None => Unauthorized(Json.obj("error" -> "Invalid token"))
            }
          case None => Unauthorized(Json.obj("error" -> "Invalid token"))
        }
      case _ => Unauthorized(Json.obj("error" -> "No token provided"))
    }
  }

}
