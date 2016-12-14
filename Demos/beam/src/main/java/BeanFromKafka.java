import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.runners.flink.translation.wrappers.streaming.io.UnboundedFlinkSource;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer08;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.joda.time.Duration;

import java.util.Properties;

public class BeanFromKafka {

    static void main(String[] args){

        PipelineOptionsFactory.register(KafkaStreamingWordCountOptions.class);

        KafkaStreamingWordCountOptions options = PipelineOptionsFactory.fromArgs(args)
                .as(KafkaStreamingWordCountOptions.class);

        options.setJobName("KafkaExample - WindowSize: " + options.getWindowSize() + " seconds");
        options.setStreaming(true);
        options.setCheckpointingInterval(1000L);
        options.setNumberOfExecutionRetries(5);
        options.setExecutionRetryDelay(3000L);
        options.setRunner(FlinkRunner.class);

        Pipeline pipe = Pipeline.create(options);

        Properties p = new Properties();
        p.setProperty("zookeeper.connect", options.getZookeeper());
        p.setProperty("bootstrap.servers", options.getBroker());
        p.setProperty("group.id", options.getGroup());

        // this is the Flink consumer that reads the input to
        // the program from a kafka topic.
        FlinkKafkaConsumer08<String> kafkaConsumer = new FlinkKafkaConsumer08<>(
                options.getKafkaTopic(),
                new SimpleStringSchema(), p);

        PCollection<String> words = pipe
                .apply("StreamingWordCount", Read.from(UnboundedFlinkSource.of(kafkaConsumer)))
                .apply(ParDo.of(new ExtractWordsFn()))
                .apply(Window.<String>into(FixedWindows.of(
                        Duration.standardSeconds(options.getWindowSize())))
                        .triggering(AfterWatermark.pastEndOfWindow()).withAllowedLateness(Duration.ZERO)
                        .discardingFiredPanes());

        PCollection<KV<String, Long>> wordCounts =
                words.apply(Count.<String>perElement());

        wordCounts.apply(ParDo.of(new FormatAsStringFn()))
                .apply(TextIO.Write.to("./outputKafka.txt"));

        pipe.run();

    }

    public static class FormatAsStringFn extends DoFn<KV<String, Long>, String> {

        @DoFn.ProcessElement
        public void processElement(ProcessContext c) {
            
            String row = c.element().getKey() + " - " + c.element().getValue() + " @ "
                    + c.timestamp().toString();
            System.out.println(row);
            c.output(row);

        }
    }

    public static class ExtractWordsFn extends DoFn<String, String> {
        private final Aggregator<Long, Long> emptyLines =
                createAggregator("emptyLines", new Sum.SumLongFn());

        @ProcessElement
        public void processElement(ProcessContext c) {
            if (c.element().trim().isEmpty()) {
                emptyLines.addValue(1L);
            }

            // Split the line into words.
            String[] words = c.element().split("[^a-zA-Z']+");

            // Output each word encountered into the output PCollection.
            for (String word : words) {
                if (!word.isEmpty()) {
                    c.output(word);
                }
            }
        }

    }

}
