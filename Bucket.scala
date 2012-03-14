import scala.actors.Actor
import scala.actors.Actor._

object KilledSig{}

class Bucket(bucket_id: String) extends Actor {

  // println("new bucket: " + bucket_id)

  val created_at = new java.util.Date()
  var last_append_at = new java.util.Date()

  var data = Set[String]()

  def act() = { 
    Actor.loop{ react{
      case HearbeatSig => heartbeat
      case KilledSig   => killed
      case msg: String => append(msg)
    }}
  }

  
  def append(msg: String) = {
    last_append_at = new java.util.Date()
    data += msg
  }


  def heartbeat() = {
    val now = new java.util.Date()

    val since_append = (now.getTime() - last_append_at.getTime())
    val since_create = (now.getTime() - created_at.getTime())

    if (since_append > (Kollekt.config("bucket_timeout") * 1000))
      BucketFactory.kill(bucket_id)

    if (since_create > (Kollekt.config("bucket_maxage") * 1000))
      BucketFactory.kill(bucket_id)

    if (data.size > Kollekt.config("bucket_maxsize"))
      BucketFactory.kill(bucket_id)
 
  }


  def killed() =
    Kollekt.writer ! ((bucket_id, data))

 
}