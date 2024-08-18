package actions

import play.api.libs.json.Json
import play.api.mvc._
import utils.JwtUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JwtAuthenticatedAction @Inject()(parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val authHeader = request.headers.get("Authorization")
    authHeader.map { header =>
      if (header.startsWith("Bearer ")) {
        val token = header.substring(7)
        JwtUtil.validateToken(token) match {
          case Some(claim) =>
            JwtUtil.extractUsername(claim) match {
              case Some(username) =>
                // Add the username to the request attributes if needed
                block(request).map { result =>
                  result.withHeaders("X-Authenticated-Username" -> username)
                }
              case None =>
                Future.successful(Results.Unauthorized(Json.obj("error" -> "Invalid token")))
            }
          case None =>
            Future.successful(Results.Unauthorized(Json.obj("error" -> "Invalid or missing token")))
        }
      } else {
        Future.successful(Results.Unauthorized(Json.obj("error" -> "Invalid token format")))
      }
    }.getOrElse(Future.successful(Results.Unauthorized(Json.obj("error" -> "Missing Authorization header"))))
  }
}
