import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by roadan on 12/13/16.
 */
public class SimpleStream {

    static void main() {

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> textLines = builder.stream(stringSerde, stringSerde, "streams-file-input");

        KStream<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .map((key, value) -> new KeyValue<>(value, value))
                .countByKey(stringSerde, "Counts")
                .toStream();

        wordCounts.foreach(new ForeachAction<String, Long>() {
            @Override
            public void apply(String key, Long value) {
                System.out.println(key + " " + value.toString());
            }
        });

        wordCounts.to(stringSerde, longSerde, "streams-wordcount-output");

        // connecting to the Kafka Cluster
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "map-function-scala-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");

        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsConfig streamingConfig = new StreamsConfig(props);

        KafkaStreams stream = new KafkaStreams(builder, streamingConfig);
        stream.start();
    }
}
