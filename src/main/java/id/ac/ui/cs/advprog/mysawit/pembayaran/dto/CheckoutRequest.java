package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

import java.math.BigDecimal;

public class CheckoutRequest {
    private Long orderId;
    private BigDecimal amount;
    private String customerName;
    private String customerEmail;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
