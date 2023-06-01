package org.company.client.messaging;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.company.client.configuration.ApplicationConfiguration;
import org.company.client.configuration.KafkaConfiguration;
import org.company.client.service.TransactionRequestExecutor;
import org.company.consumer.AbstractConsumer;
import org.company.context.ApplicationContext;
import org.company.context.Bean;
import org.company.dto.PlayerCreatedEvent;
import org.company.util.MessageDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PlayerEventConsumer extends AbstractConsumer<Object, PlayerCreatedEvent> implements Bean {

    private static final Logger log = LoggerFactory.getLogger(PlayerEventConsumer.class);

    private KafkaConfiguration configuration;

    private TransactionRequestExecutor requestExecutor;

    @Override
    public void init(ApplicationContext context) {
        configuration = context.getBean(ApplicationConfiguration.class).getKafka();
        requestExecutor = context.getBean(TransactionRequestExecutor.class);
    }

    @Override
    protected String name() {
        return "player-event-consumer";
    }

    @Override
    protected void process(PlayerCreatedEvent message) {
        log.info("Received {}", message);
        requestExecutor.executeTransactionRequestWorker(message.player());
    }

    @Override
    protected int consumerCount() {
        return 1;
    }

    @Override
    protected Map<String, Object> consumerConfigs() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getBootstrapServers(),
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class,
            ConsumerConfig.GROUP_ID_CONFIG, configuration.getGroupId()
        );
    }

    @Override
    protected String topic() {
        return configuration.getPlayerEventTopic();
    }
}
