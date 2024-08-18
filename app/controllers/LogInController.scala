package controllers

import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import services.{AuthService, LoginRequest}
import utils.JwtUtil

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LogInController @Inject()(
                                 cc: ControllerComponents,
                                 authService: AuthService,
                                 override val messagesApi: MessagesApi // Add this line
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) with I18nSupport {

  val loginForm: Form[LoginRequest] = LoginRequest.form
  private val logger = Logger(getClass)

  def login: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    logger.info(s"Login Method call ")
    Ok(views.html.login(loginForm))
  }

  def submit: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.login(formWithErrors))),
      loginData => {
        authService.authenticate(loginData.username, loginData.password).map {
          case Some(user) =>
            val token = JwtUtil.createToken(user.userName)
            Ok(Json.obj("token" -> token))
          case None =>
            Unauthorized(Json.obj("error" -> "Invalid username or password"))
        }
      }
    )
  }
}
