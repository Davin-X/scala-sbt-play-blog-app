package handlers

import exceptions.{UserAlreadyExistsException, UserNotFoundException, ValidationException}
import play.api.http.HttpErrorHandler
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND}
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._

import javax.inject.Singleton
import scala.concurrent._

@Singleton
class ErrorHandler extends HttpErrorHandler {

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful {
      statusCode match {
        case BAD_REQUEST => BadRequest(Json.obj("error" -> message))
        case NOT_FOUND => NotFound(Json.obj("error" -> message))
        case _ => Status(statusCode)(Json.obj("error" -> message))
      }
    }
  }

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    exception match {
      case e: UserAlreadyExistsException => Future.successful(Conflict(Json.obj("error" -> e.getMessage)))
      case e: UserNotFoundException => Future.successful(NotFound(Json.obj("error" -> e.getMessage)))
      case e: ValidationException => Future.successful(BadRequest(Json.obj("error" -> e.getMessage)))
      case _ => Future.successful(InternalServerError(Json.obj("error" -> "An unexpected error occurred")))
    }
  }
}
