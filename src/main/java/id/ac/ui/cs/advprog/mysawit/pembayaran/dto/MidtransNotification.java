package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MidtransNotification {
    @JsonProperty("transaction_time")
    private String transactionTime;

    @JsonProperty("transaction_status")
    private String transactionStatus;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("status_code")
    private String statusCode;

    @JsonProperty("signature_key")
    private String signatureKey;

    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("gross_amount")
    private String grossAmount;

    @JsonProperty("fraud_status")
    private String fraudStatus;

    @JsonProperty("currency")
    private String currency;

    public String getTransactionStatus() { return transactionStatus; }
    public String getStatusCode() { return statusCode; }
    public String getSignatureKey() { return signatureKey; }
    public String getOrderId() { return orderId; }
    public String getGrossAmount() { return grossAmount; }
    public String getFraudStatus() { return fraudStatus; }
}
