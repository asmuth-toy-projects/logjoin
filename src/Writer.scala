import scala.actors.Actor
import scala.actors.Actor._
import java.io.FileWriter;
import java.io.PrintWriter;

class Writer extends Actor {

  def act(){ 
    Actor.loop{ react{
      case msg: (String, Array[String]) => (persist _) tupled msg
    }}
  }

  def persist(bucket_id: String, data: Array[String]) = {
    Kollekt.stats("buckets_persisted") += 1
    val now = new java.util.Date()
    var now_ts = now.getTime() / 1000

    val csv_head = now.getTime().toString() + ";" + bucket_id;
    val csv_str = (csv_head /: data)(_ + ";" + _)

    val csv_length = Kollekt.config("out_filelength")
    val csv_time = ((now_ts / csv_length) * csv_length).toString 
    val csv_file = Kollekt.output_dir + "/" + csv_time + ".csv"

    append(csv_file, csv_str)
  }

  def append(fileName:String, textData:String) =
    using (new FileWriter(fileName, true)){ 
      fileWriter => using (new PrintWriter(fileWriter)) {
        printWriter => printWriter.println(textData)
      }
  }

  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B =
    try { f(param) } finally { param.close() }

}