import kafka.serializer.StringEncoder;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sela on 12/12/16.
 */
public class SparkCpuStatsSQL {
    public static void main(String[] args) {
        Map<String, String> kafkaParams2 = new HashMap<>();
        kafkaParams2.put("kafka.bootstrap.servers", "localhost:9092");
        kafkaParams2.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams2.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams2.put("group.id", "spark_cpustats_java_strctured");
        kafkaParams2.put("auto.offset.reset", "latest");
        kafkaParams2.put("enable.auto.commit", "false");
        kafkaParams2.put("subscribe", "cpuMetrics");

        SparkSession spark = SparkSession.builder().master("local").appName("spark_cpustats_java_structuredStreaming").getOrCreate();

        Dataset<Row> logs = spark.readStream().format("kafka").options(kafkaParams2).load();
        Dataset<CpuMetricMessage> messages = logs.map((MapFunction<Row, CpuMetricMessage>) row -> {
            String jsonStr = row.getString(2);
            return CpuMetricMessage.createInstance(jsonStr);
        }, Encoders.javaSerialization(CpuMetricMessage.class));
        StreamingQuery query = messages.writeStream().outputMode(OutputMode.Append()).format("console").start();
        query.awaitTermination();
    }

}