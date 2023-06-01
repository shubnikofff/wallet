package org.company.configuration;

public class KafkaConfiguration {

    private String bootstrapServers;

    private String transactionRequestTopic;

    private String transactionResponseTopic;

    private String playerEventTopic;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getTransactionRequestTopic() {
        return transactionRequestTopic;
    }

    public void setTransactionRequestTopic(String transactionRequestTopic) {
        this.transactionRequestTopic = transactionRequestTopic;
    }

    public String getTransactionResponseTopic() {
        return transactionResponseTopic;
    }

    public void setTransactionResponseTopic(String transactionResponseTopic) {
        this.transactionResponseTopic = transactionResponseTopic;
    }

    public String getPlayerEventTopic() {
        return playerEventTopic;
    }

    public void setPlayerEventTopic(String playerEventTopic) {
        this.playerEventTopic = playerEventTopic;
    }
}
