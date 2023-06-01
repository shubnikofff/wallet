package org.company.server.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.company.server.configuration.ApplicationConfiguration;
import org.company.dto.TransactionRequest;
import org.company.serialization.MessageDeserializer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionConsumer implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private int workerCount;

    private String kafkaTopic;

    private Map<String, Object> kafkaConsumerConfigs;

    private ExecutorService executorService;

    private TransactionProcessor transactionProcessor;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(ApplicationConfiguration.class);

        kafkaConsumerConfigs = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getKafka().getBootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, configuration.getKafka().getGroupId()
        );

        kafkaTopic = configuration.getKafka().getTransactionRequestTopic();
        workerCount = configuration.getWorkerCount();
        executorService = Executors.newFixedThreadPool(workerCount);
        transactionProcessor = context.getBean(TransactionProcessor.class);
    }

    public void start() {
        log.info("Starting transaction consumers");
        for (int i = 0; i < workerCount; i++) {
            final var kafkaConsumer = new KafkaConsumer<UUID, TransactionRequest>(kafkaConsumerConfigs);
            kafkaConsumer.subscribe(Collections.singleton(kafkaTopic));
            executorService.submit(() -> consume(kafkaConsumer));
        }
    }

    public void stop() {
        log.info("Stopping transaction consumers");
        executorService.shutdown();
    }

    private void consume(KafkaConsumer<UUID, TransactionRequest> kafkaConsumer) {
        while (!executorService.isShutdown() || !executorService.isTerminated()) {
            kafkaConsumer.poll(Duration.ofMillis(100)).forEach(message -> {
                final var transaction = message.value();
                log.info("Received transaction: {}", transaction);
                transactionProcessor.process(transaction);
            });
        }
    }
}
