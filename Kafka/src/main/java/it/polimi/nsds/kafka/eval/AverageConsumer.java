package it.polimi.nsds.kafka.eval;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class AverageConsumer {
    private static final String serverAddr = "localhost:9092";
    private static final String inputTopic = "inputTopic";

    private static final boolean autoCommit = true;
    private static final int autoCommitIntervalMs = 15000;

    private static final String offsetResetStrategy = "latest";

    public static void main(String[] args) {
        final String consumerGroupId = args[0];
        final HashMap<String, Integer> map = new HashMap<>();
        float oldAvg = 0;
        float newAvg;
        int sum;

        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serverAddr);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(autoCommit));
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(autoCommitIntervalMs));

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetResetStrategy);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());


        KafkaConsumer<String, Integer> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(inputTopic));

        while (true) {
            final ConsumerRecords<String, Integer> records = consumer.poll(Duration.of(5, ChronoUnit.MINUTES));

            for (final ConsumerRecord<String, Integer> record : records) {
                if(map.containsKey(record.key())) {
                    map.replace(record.key(), record.value());
                }
                else {
                    map.put(record.key(), record.value());
                }

                //Average
                sum = 0;
                for(String key : map.keySet()) {
                    sum += map.get(key);
                }
                newAvg = (float)sum / map.keySet().size();

                //Print if changed
                if(oldAvg != newAvg) {
                    System.out.println( "Consumer group: " + consumerGroupId +
                            "\tPartition: " + record.partition() +
                            "\tOffset: " + record.offset() +
                            "\tAverage: " + newAvg
                    );
                }
                oldAvg = newAvg;
            }
        }
    }
}