package controllers

import entities.User
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
        }
      }
    )
  }

  // Create multiple users
  def createMultipleUsers: Action[JsValue] = Action.async(parse.json) { request =>
    logger.info(s"API call: createMultipleUsers, request: ${request.body}")
    request.body.validate[List[User]].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> JsError.toJson(errors)))),
      userDtoList => {
        userService.createMultipleUsers(userDtoList).map { ids =>
          Created(Json.obj("ids" -> ids))
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
        }
      }
    )
  }

  def getUser(id: Int): Action[AnyContent] = Action.async {
    logger.info(s"API call: getUser, id: $id")
    userService.getUser(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> "User not found"))
    }
  }

  def deleteUser(id: Int): Action[AnyContent] = Action.async {
    logger.info(s"API call: deleteUser, id: $id")
    userService.deleteUser(id).map {
      case 0 => NotFound(Json.obj("message" -> "User not found"))
      case _ => Ok(Json.obj("message" -> "User deleted"))
    }
  }

  def getAllUsers: Action[AnyContent] = Action.async {
    logger.info("API call: getAllUsers")
    userService.getAllUsers.map { users =>
      Ok(Json.toJson(users))
    }
  }
}