package org.company.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConsumer<K, V> {

    private static final Logger log = LoggerFactory.getLogger(AbstractConsumer.class);

//    private final String name;
//
//    private final Consumer<V> task;
//
//    private final int consumerCount;

    private ExecutorService executorService;

//    private final Map<String, Object> kafkaConfigs;
//
//    private final String kafkaTopic;

//    public AbstractConsumer(String name, int consumerCount, Consumer<V> task, Map<String, Object> kafkaConfigs, String kafkaTopic) {
//        this.name = name;
//        this.task = task;
//        this.consumerCount = consumerCount;
//        this.kafkaConfigs = kafkaConfigs;
//        this.kafkaTopic = kafkaTopic;
//        executorService = Executors.newFixedThreadPool(consumerCount, new ConsumerThreadFactory(name));
//    }

    public void start() {
        log.info("Starting {} consumers", name());
        executorService = Executors.newFixedThreadPool(consumerCount(), new ConsumerThreadFactory(name()));
        for (int i = 0; i < consumerCount(); i++) {
            final var kafkaConsumer = new KafkaConsumer<K, V>(consumerConfigs());
            kafkaConsumer.subscribe(Collections.singleton(topic()));
            executorService.submit(() -> consume(kafkaConsumer));
        }
    }

    public void stop() {
        if (executorService != null) {
            log.info("Stopping {} consumers", name());
            executorService.shutdown();
        }
    }

    private void consume(KafkaConsumer<K, V> consumer) {
        while (!executorService.isShutdown() || !executorService.isTerminated()) {
            consumer.poll(Duration.ofMillis(100)).forEach(message -> process(message.value()));
        }
    }

    protected abstract String name();

    protected abstract void process(V message);

    protected abstract int consumerCount();

    protected abstract Map<String, Object> consumerConfigs();

    protected abstract String topic();
}
