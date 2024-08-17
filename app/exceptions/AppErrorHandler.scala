package exceptions

class UserAlreadyExistsException(message: String) extends Exception(message)
class UserNotFoundException(message: String) extends Exception(message)
class ValidationException(message: String) extends Exception(message)
