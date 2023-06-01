package org.company.model;

import java.math.BigDecimal;

public class Wallet {

    private final String username;

    private long balanceVersion;

    private BigDecimal balance;

    public Wallet(String username, long balanceVersion, BigDecimal balance) {
        this.username = username;
        this.balanceVersion = balanceVersion;
        this.balance = balance;
    }

    public void setBalanceVersion(long balanceVersion) {
        this.balanceVersion = balanceVersion;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public long getBalanceVersion() {
        return balanceVersion;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
               "username='" + username + '\'' +
               ", balanceVersion=" + balanceVersion +
               ", balance=" + balance +
               '}';
    }
}
