package org.company.server.messaging;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.PlayerCreatedEvent;
import org.company.dto.TransactionResponse;
import org.company.util.MessageSerializer;
import org.company.server.configuration.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MessagePublisher implements Bean {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private KafkaProducer<Object, TransactionResponse> transactionResponseProducer;

    private KafkaProducer<Object, PlayerCreatedEvent> playerEventProducer;

    private String transactionResponseTopic;

    private String playerEventTopic;

    @Override
    public void init(ApplicationContext context) {
        final var configuration = context.getBean(ApplicationConfiguration.class).getKafka();
        final Map<String, Object> config = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class
        );

        transactionResponseProducer = new KafkaProducer<>(config);
        playerEventProducer = new KafkaProducer<>(config);
        transactionResponseTopic = configuration.getTransactionResponseTopic();
        playerEventTopic = configuration.getPlayerEventTopic();
    }

    public void publish(TransactionResponse message) {
        log.info("Publish {}", message);
        final var producerRecord = new ProducerRecord<>(transactionResponseTopic, message);
        transactionResponseProducer.send(producerRecord);
    }

    public void publish(PlayerCreatedEvent message) {
        log.info("Publish {}", message);
        final var producerRecord = new ProducerRecord<>(playerEventTopic, message);
        playerEventProducer.send(producerRecord);
    }
}
