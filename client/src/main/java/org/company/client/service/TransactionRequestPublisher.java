package org.company.client.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionRequest;
import org.company.model.Player;
import org.company.serialization.MessageSerializer;


import java.util.Map;
import java.util.UUID;

public class TransactionRequestPublisher implements Bean {

    private KafkaProducer<UUID, TransactionRequest> producer;

    private String topic;

    @Override
    public void init(ApplicationContext context) {
        final var kafkaConfiguration = context.getBean(ApplicationConfiguration.class).getKafka();

        final Map<String, Object> config = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class
        );

        producer = new KafkaProducer<>(config);
        topic = kafkaConfiguration.getTransactionRequestTopic();
    }

//    public static TransactionRequestPublisher getInstance(ApplicationConfiguration configuration) {
//        final var instance = new TransactionRequestPublisher();
//
//        final Map<String, Object> config = Map.of(
//            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getKafka().getBootstrapServers(),
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class
//        );
//
//        instance.producer = new KafkaProducer<>(config);
//        instance.topic = configuration.getKafka().getTransactionRequestTopic();
//
//        return instance;
//    }

    public void publish(TransactionRequest transactionRequest, Player player) {
        final var message = new ProducerRecord<>(topic, player.id(), transactionRequest);
        producer.send(message);
    }

    public void disconnect() {
        producer.close();
    }
}
