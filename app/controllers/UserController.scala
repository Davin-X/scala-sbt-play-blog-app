package controllers

import entities.User
import exceptions.UserAlreadyExistsException
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import services.UserService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val userFormat: OFormat[User] = Json.format[User]

  private val logger = Logger(getClass)

  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    logger.info(s"API call: createUser, request: ${request.body}")
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
      user => {
        userService.createUser(user).map { id =>
          Created(Json.obj("id" -> id))
        }.recover {
          case e: UserAlreadyExistsException => Conflict(Json.obj("error" -> e.getMessage))
          case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
        }
      }
    )
  }

  def createMultipleUsers: Action[JsValue] = Action.async(parse.json) { request =>
    logger.info(s"API call: createMultipleUsers, request: ${request.body}")
    request.body.validate[List[User]].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
      userDtoList => {
        userService.createMultipleUsers(userDtoList).map { ids =>
          Created(Json.obj("ids" -> ids))
        }.recover {
          case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
        }
      }
    )
  }

  def updateUser(id: Int): Action[JsValue] = Action.async(parse.json) { request =>
    logger.info(s"API call: updateUser, id: $id, request: ${request.body}")
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
      user => {
        userService.updateUser(user, id).map {
          case 0 => NotFound(Json.obj("message" -> "User not found"))
          case _ => Ok(Json.obj("message" -> "User updated"))
        }.recover {
          case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
        }
      }
    )
  }

  def getUser(id: Int): Action[AnyContent] = Action.async {
    logger.info(s"API call: getUser, id: $id")
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }.recover {
      case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
    }
  }

  def getUserByMail(mail: String): Action[AnyContent] = Action.async {
    logger.info(s"API call: getUserByMail, mail: $mail")
    userService.getUserByMail(mail).map {
      case Some(user) => Ok(Json.toJson(user))
    }
  }

  def deleteUser(id: Int): Action[AnyContent] = Action.async {
    logger.info(s"API call: deleteUser, id: $id")
    userService.deleteUser(id).map {
      case 0 => NotFound(Json.obj("message" -> "User not found"))
      case _ => Ok(Json.obj("message" -> "User deleted"))
    }.recover {
      case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
    }
  }

  def getAllUsers: Action[AnyContent] = Action.async {
    logger.info("API call: getAllUsers")
    userService.getAllUsers.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case e: Exception => InternalServerError(Json.obj("error" -> "An unexpected error occurred"))
    }
  }
}
