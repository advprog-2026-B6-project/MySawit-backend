package id.ac.ui.cs.advprog.mysawit.pembayaran.dto;

public class CheckoutResponse {
    private String token;
    private String redirectUrl;

    public CheckoutResponse(String token, String redirectUrl) {
        this.token = token;
        this.redirectUrl = redirectUrl;
    }

    public String getToken() { return token; }
    public String getRedirectUrl() { return redirectUrl; }
}
