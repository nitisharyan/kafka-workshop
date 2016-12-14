import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

/**
 * Created by roadan on 12/14/16.
 */
public interface KafkaStreamingWordCountOptions extends PipelineOptions, FlinkPipelineOptions {

    static final String KAFKA_TOPIC = "test";  // Default kafka topic to read from
    static final String KAFKA_BROKER = "localhost:9092";  // Default kafka broker to contact
    static final String GROUP_ID = "myGroup";  // Default groupId
    static final String ZOOKEEPER = "localhost:2181";  // Default zookeeper to connect to for Kafka
    static final long WINDOW_SIZE = 10;  // Default window duration in seconds
    static final long SLIDE_SIZE = 5;  // Default window slide in seconds


    @Description("The Kafka topic to read from")
    @Default.String(KAFKA_TOPIC)
    String getKafkaTopic();

    void setKafkaTopic(String value);

    @Description("The Kafka Broker to read from")
    @Default.String(KAFKA_BROKER)
    String getBroker();

    void setBroker(String value);

    @Description("The Zookeeper server to connect to")
    @Default.String(ZOOKEEPER)
    String getZookeeper();

    void setZookeeper(String value);

    @Description("The groupId")
    @Default.String(GROUP_ID)
    String getGroup();

    void setGroup(String value);

    @Description("Sliding window duration, in seconds")
    @Default.Long(WINDOW_SIZE)
    Long getWindowSize();

    @Description("Window slide, in seconds")
    @Default.Long(SLIDE_SIZE)
    Long getSlide();

    void setSlide(Long value);


}

