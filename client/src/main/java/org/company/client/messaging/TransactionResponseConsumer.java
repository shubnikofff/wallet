package org.company.client.messaging;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.consumer.AbstractConsumer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionResponse;
import org.company.util.MessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class TransactionResponseConsumer extends AbstractConsumer<Object, TransactionResponse> implements Bean {

    private static final Logger log = LoggerFactory.getLogger(TransactionResponseConsumer.class);

    private ApplicationConfiguration configuration;

    @Override
    public void init(ApplicationContext context) {
        configuration = context.getBean(ApplicationConfiguration.class);
    }

    @Override
    protected String name() {
        return "transaction-response-consumer";
    }

    @Override
    protected void process(TransactionResponse message) {
        log.info("Received {}", message);
    }

    @Override
    protected int consumerCount() {
        return configuration.getTransaction().getResponseConsumerCount();
    }

    @Override
    protected Map<String, Object> consumerConfigs() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getKafka().getBootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, configuration.getKafka().getGroupId()
        );
    }

    @Override
    protected String topic() {
        return configuration.getKafka().getTransactionResponseTopic();
    }
}
