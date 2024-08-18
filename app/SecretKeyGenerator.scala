import java.security.SecureRandom
import java.util.Base64

object SecretKeyGenerator {
  def generateKey(length: Int = 32): String = {
    val random = new SecureRandom()
    val key = new Array[Byte](length)
    random.nextBytes(key)
    Base64.getEncoder.encodeToString(key)
  }

  def main(args: Array[String]): Unit = {
    println(generateKey()) // Print a new secret key
  }
}
