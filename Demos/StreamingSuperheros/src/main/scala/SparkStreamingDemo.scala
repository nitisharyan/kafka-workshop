import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamingDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Spark Streaming Demo").setMaster("local")
    val ssc = new StreamingContext(conf, Seconds(10))

    val lines = ssc.socketTextStream("localhost", 9999)

    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}