package services

import entities.{User, UserDAO}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthService @Inject()(userDAO: UserDAO)(implicit ec: ExecutionContext) {

  def authenticate(username: String, password: String): Future[Option[User]] = {
    userDAO.findByUsername(username).map {
      case Some(user) if PasswordUtil.checkPassword(password, user.password) =>
        Some(user)
      case _ => None
    }
  }
}
