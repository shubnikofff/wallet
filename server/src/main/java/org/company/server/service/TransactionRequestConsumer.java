package org.company.server.service;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.company.consumer.AbstractConsumer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionRequest;
import org.company.serialization.MessageDeserializer;
import org.company.server.configuration.ApplicationConfiguration;

import java.util.Map;
import java.util.UUID;

public class TransactionRequestConsumer extends AbstractConsumer<UUID, TransactionRequest> implements Bean {

    private ApplicationConfiguration configuration;

    private TransactionProcessor transactionProcessor;

    @Override
    public void init(ApplicationContext context) {
        configuration = context.getBean(ApplicationConfiguration.class);
        transactionProcessor = context.getBean(TransactionProcessor.class);
    }

    @Override
    protected String name() {
        return "transaction-request-consumer";
    }

    @Override
    protected void process(TransactionRequest message) {
        transactionProcessor.process(message);
    }

    @Override
    protected int consumerCount() {
        return configuration.getWorkerCount();
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
        return configuration.getKafka().getTransactionRequestTopic();
    }
}
