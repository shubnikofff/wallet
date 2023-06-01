package org.company.server.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.company.server.configuration.ApplicationConfiguration;
import org.company.dto.TransactionResponse;
import org.company.serialization.MessageSerializer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransactionResponsePublisher implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionResponsePublisher.class);

    private KafkaProducer<Object, TransactionResponse> producer;

    private String topic;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(ApplicationConfiguration.class).getKafka();
        final Map<String, Object> config = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class
        );

        producer = new KafkaProducer<>(config);
        topic = configuration.getTransactionResponseTopic();
    }

    public void publish(TransactionResponse transactionResponse) {
        log.info("Publishing: {}", transactionResponse);
        final var producerRecord = new ProducerRecord<>(topic, transactionResponse);
        producer.send(producerRecord);
    }
}
