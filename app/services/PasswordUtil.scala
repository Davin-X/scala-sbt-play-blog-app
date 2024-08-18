package services

import com.github.t3hnar.bcrypt._

object PasswordUtil {
  def hashPassword(password: String): String = password.bcrypt

  def checkPassword(password: String, hash: String): Boolean = password.isBcrypted(hash)
}
