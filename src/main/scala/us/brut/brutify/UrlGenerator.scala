package us.brut.brutify
import scala.util.Random
import scala.util.control.Exception._

object UrlGenerator {
  val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  val base = chars.length()
  // Generate a random url of 3 chars
  def url(length : Int = 3) = {
    urlGeneric(length, UrlGenerator.randomDefault)
  }
  // Give me a different one
  def diff(givenUrl : String) = {
    url(givenUrl.length + 1)
  }
  // Different url generator functions are passed in as arguments if needed be
  def urlGeneric(length : Int, f: Int => String) = {
    f(length)
  }
  // Default generator
  def randomDefault(length: Int) : String = {
    def recur(length: Int, url: List[Char]) : List[Char] = {
      if (length == 0) url
      else recur(length - 1, chars(Random.nextInt(base)) :: url)
    }
    recur(length, List()).mkString
  }
}