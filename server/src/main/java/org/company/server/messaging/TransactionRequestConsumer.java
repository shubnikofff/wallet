package org.company.server.messaging;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.company.consumer.AbstractConsumer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.TransactionRequest;
import org.company.serialization.MessageDeserializer;
import org.company.server.configuration.ApplicationConfiguration;
import org.company.server.service.TransactionProcessor;

import java.util.Map;

public class TransactionRequestConsumer extends AbstractConsumer<String, TransactionRequest> implements Bean {

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
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, configuration.getKafka().getGroupId()
        );
    }

    @Override
    protected String topic() {
        return configuration.getKafka().getTransactionRequestTopic();
    }
}
