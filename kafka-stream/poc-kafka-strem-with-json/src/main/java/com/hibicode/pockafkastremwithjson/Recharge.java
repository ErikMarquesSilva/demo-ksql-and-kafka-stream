package com.hibicode.pockafkastremwithjson;

public class Recharge {

    private String amount;
    private String clientId;

    public Recharge() {
    }

    public Recharge(String amount, String clientId) {
        this.amount = amount;
        this.clientId = clientId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
