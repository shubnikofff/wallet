package org.company.client.configuration;

import java.math.BigDecimal;

public class TransactionConfiguration {

    private int responseConsumerCount;

    private BigDecimal generatedAmountMaximum;

    private long delayBetweenRequestsMillis;

    public int getResponseConsumerCount() {
        return responseConsumerCount;
    }

    public void setResponseConsumerCount(int responseConsumerCount) {
        this.responseConsumerCount = responseConsumerCount;
    }

    public BigDecimal getGeneratedAmountMaximum() {
        return generatedAmountMaximum;
    }

    public void setGeneratedAmountMaximum(BigDecimal generatedAmountMaximum) {
        this.generatedAmountMaximum = generatedAmountMaximum;
    }

    public long getDelayBetweenRequestsMillis() {
        return delayBetweenRequestsMillis;
    }

    public void setDelayBetweenRequestsMillis(long delayBetweenRequestsMillis) {
        this.delayBetweenRequestsMillis = delayBetweenRequestsMillis;
    }
}
