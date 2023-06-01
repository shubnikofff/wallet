package org.company.client.messaging;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionRequest;
import org.company.util.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransactionRequestPublisher implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestPublisher.class);

    private KafkaProducer<String, TransactionRequest> producer;

    private String topic;

    @Override
    public void init(ApplicationContext context) {
        final var kafkaConfiguration = context.getBean(ApplicationConfiguration.class).getKafka();
        final Map<String, Object> config = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class
        );

        producer = new KafkaProducer<>(config);
        topic = kafkaConfiguration.getTransactionRequestTopic();
    }

    public void publish(TransactionRequest transactionRequest) {
        log.info("Publish {}", transactionRequest);
        final var message = new ProducerRecord<>(topic, transactionRequest.username(), transactionRequest);
        producer.send(message);
    }

    public void disconnect() {
        producer.close();
    }
}
